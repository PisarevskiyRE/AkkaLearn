package chapter4

import akka.actor.SupervisorStrategy.{Restart, Resume, Stop}
import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, OneForOneStrategy, PoisonPill, Props, SupervisorStrategy, Terminated}
import akka.actor._
import java.io.File
import java.util.UUID

object LogProcessingApp extends App{
  val sources = Vector("file:///source1/", "file:///source2/")
  val system = ActorSystem("logprocessing")
  val databaseUrls = Vector(
    "http://mydatabase1",
    "http://mydatabase2",
    "http://mydatabase3"
  )

  system.actorOf(
    LogProcessingSupervisor.props(sources, databaseUrls),
    LogProcessingSupervisor.name)
}


/**
 *
 * возмоные ошибки акторов
 */
@SerialVersionUID(1L)
class DiskError(msg: String) extends Error(msg) with Serializable

@SerialVersionUID(1L)
class CorruptedFileException(msg: String, val file: File)  extends Exception(msg) with Serializable

@SerialVersionUID(1L)
class DbNodeDownException(msg: String) extends Exception(msg) with Serializable

@SerialVersionUID(1L)
class DbBrokenConnectionException(msg: String) extends Exception(msg) with Serializable




object LogProcessingSupervisor {
  def props(sources: Vector[String], databaseUrls: Vector[String]) =
    Props(new LogProcessingSupervisor(sources, databaseUrls))
  def name = "file-watcher-supervisor"
}

class LogProcessingSupervisor(
                               sources: Vector[String],
                               databaseUrls: Vector[String]
                             ) extends Actor with ActorLogging {

  //лист ссылок на акторы
  var fileWatchers: Vector[ActorRef] = sources.map { source =>

    val fileWatcher = context.actorOf(
      Props(new FileWatcher(source, databaseUrls))
    )
    context.watch(fileWatcher)
    fileWatcher
  }


  override def supervisorStrategy = AllForOneStrategy() {
    case _: DiskError => Stop
  }

  def receive = {
    case Terminated(fileWatcher) =>
      fileWatchers = fileWatchers.filterNot(_ == fileWatcher)
      if (fileWatchers.isEmpty) {
        log.info("Завершение работы, все наблюдатели за файлами вышли из строя.")
        context.system.terminate()
      }
  }
}




trait FileWatchingAbilities {def register(uri: String): Unit = {}}

class FileWatcher(source: String, databaseUrls: Vector[String]) extends Actor with ActorLogging with FileWatchingAbilities {
  register(source)
  override def supervisorStrategy = OneForOneStrategy() {
    case _: CorruptedFileException => Resume
  }
  val logProcessor = context.actorOf(
    LogProcessor.props(databaseUrls),
    LogProcessor.name
  )
  context.watch(logProcessor)


  import FileWatcher._
  def receive = {
    case NewFile(file, _) =>
      logProcessor ! LogProcessor.LogFile(file)
    case SourceAbandoned(uri) if uri == source =>
      log.info(s"$uri прекращена, наблюдатель за файлами остановлен.")
      self ! PoisonPill
    case Terminated(`logProcessor`) =>
      log.info(s"Обработка журнала завершилась, наблюдатель за файлами остановлен.")
      self ! PoisonPill
  }
}

object FileWatcher {
  case class NewFile(file: File, timeAdded: Long)
  case class SourceAbandoned(uri: String)
}




class LogProcessor(databaseUrls: Vector[String]) extends Actor with ActorLogging with LogParsing {
  require(databaseUrls.nonEmpty)

  val initialDatabaseUrl = databaseUrls.head
  var alternateDatabases = databaseUrls.tail

  override def supervisorStrategy = OneForOneStrategy() {
    case _: DbBrokenConnectionException => Restart
    case _: DbNodeDownException => Stop
  }


  var dbWriter = context.actorOf(
    DbWriter.props(initialDatabaseUrl),
    DbWriter.name(initialDatabaseUrl)
  )
  context.watch(dbWriter)

  import LogProcessor._

  override def receive: Receive = {
    case LogFile(file) =>
      val lines: Vector[DbWriter.Line] = parse(file)
      lines.foreach(dbWriter ! _)
    case Terminated(_) => if (alternateDatabases.nonEmpty) {
      val newDatabaseUrl = alternateDatabases.head
      alternateDatabases = alternateDatabases.tail
      dbWriter = context.actorOf(
        DbWriter.props(newDatabaseUrl),
        DbWriter.name(newDatabaseUrl)
      )
      context.watch(dbWriter)
    } else {
      log.error("Нет доступных серверов.")
      self ! PoisonPill
    }
  }
}



object LogProcessor {
  def props(databaseUrls: Vector[String]) =
    Props(new LogProcessor(databaseUrls))
  def name = s"log_processor_${UUID.randomUUID.toString}"
  // представляет новый файл журнала
  case class LogFile(file: File)
}


trait LogParsing {
  import DbWriter._
  def parse(file: File): Vector[Line] = Vector.empty[Line]
}


/**
 *
 * Актор записи в бд
 */

class DbWriter(databaseUrl: String) extends Actor {
  val connection = new DbCon(databaseUrl)

  import DbWriter._
  def receive = {
    case Line(time, message, messageType) =>
      connection.write(Map(Symbol("time") -> time,
        Symbol("message") -> message,
        Symbol("messageType") -> messageType))
  }

  override def postStop(): Unit = {
    connection.close()
  }
}
object DbWriter {
  def props(databaseUrl: String) =
    Props(new DbWriter(databaseUrl))

  def name(databaseUrl: String) =
    s"""db-writer-${databaseUrl.split("/").last}"""

  case class Line(time: Long, message: String, messageType: String)
}

class DbCon(url: String) {
  def write(map: Map[Symbol, Any]): Unit = {
  }
  def close(): Unit = {
  }
}