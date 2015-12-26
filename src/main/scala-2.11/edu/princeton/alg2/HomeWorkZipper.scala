package edu.princeton.alg2

import java.io.FileOutputStream
import java.nio.file.Paths
import java.util.zip.{ZipEntry, ZipOutputStream}

import scala.io.Source

/**
  * @author Alexey Novakov
  */
object HomeWorkZipper extends App {

  val subPackageName = "week3"
  val archiveName = "baseball.zip"
  val srcRoot = "src/main/java/"

  Paths.get(archiveName).toFile.delete

  val path = Paths.get(srcRoot, getClass.getPackage.getName.replace(".", "/"), subPackageName)
  val zip = new ZipOutputStream(new FileOutputStream(archiveName))

  try {
    for (file <- path.toFile.listFiles) {
      val contentWithoutPackage = Source.fromFile(file).getLines().toList.tail.mkString("\n")
      zip.putNextEntry(new ZipEntry(file.getName))
      using(zip)(_.write(contentWithoutPackage.getBytes))
    }
  } finally zip.close

  def using[A <: {def closeEntry() : Unit}, B](resource: A)(f: A => B): B = try f(resource) finally resource.closeEntry
}
