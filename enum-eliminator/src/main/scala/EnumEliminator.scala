
import scala.collection.mutable.{HashMap => MHashMap}
import scala.io.Source

import java.io.File

object EnumEliminator {

  val EnumDef     = """((?:(?:public|protected|private|static)\s+)*)enum\s+([A-Za-z][A-Za-z0-9_]*)\s*\{\s*((?:[A-Za-z][A-Za-z0-9_]*\s*,\s*)*[A-Za-z][A-Za-z0-9_]*)\s*;?\s*\}""".r
  val SwitchCase  = """case\s+([A-Za-z][A-Za-z0-9_]*)\s*:""".r
  val TypeUseDecl = """([A-Za-z][A-Za-z0-9_]*)\s""".r
  val PackageDecl = """package\s+([A-Za-z0-9_.]+)\s*;""".r

  def main(args : Array[String]) : Unit = {
    if (args.size <= 1) {
      println("Usage: EnumEliminator <source-files> <target-directory>")
    } else {
      val targetDir = args.last
      val sourceFiles =
        findJavaFiles(for (str <- args.init) yield new File(str)).toVector

      println("Processing " + (sourceFiles mkString ", "))
      val contents =
        for (f <- sourceFiles) yield (f, Source.fromFile(f).getLines.mkString("\n"))

      val typeSubst, caseSubst = new MHashMap[String, String]
      val contents2 =
        for ((f, str) <- contents) yield {
          val (newStr, ts, cs) = rewriteEnums(str)
          typeSubst ++= ts
          caseSubst ++= cs
          (f, newStr)
        }

      val contents3 =
        for ((f, str) <- contents2) yield {
          (f, rewriteTypes(rewriteCases(str, caseSubst.toMap), typeSubst.toMap))
        }

//      println(contents3)
      for ((f, str) <- contents3) {
        val pac =
          (for (m <- PackageDecl.findFirstMatchIn(str)) yield m.group(1)).getOrElse("")
        val targetDir2 = new File(targetDir, pac.replaceAll("\\.", "/"))
        val targetFile = new File(targetDir2, f.getName)

        println("Writing " + targetFile + " ...")
        targetFile.getParentFile.mkdirs
        val out = new java.io.FileOutputStream(targetFile)
        Console.withOut(out) {
          println(str)
        }
        out.close
      }
    }
  }

  def rewriteEnums(str : String) : (String,
                                    Map[String, String],
                                    Map[String, String]) = {
    val typeSubst, caseSubst = new MHashMap[String, String]
    val res =
      EnumDef.replaceAllIn(str, {
                             m =>
                             val t = m.group(2).trim
                             println("Rewriting enum " + t)
                             typeSubst.put(t, "int")
                             val members =
                               (for ((c, n) <-
                                     m.group(3).split(",").zipWithIndex) yield {
                                  val ca = c.trim
                                  caseSubst.put(ca, t + "." + ca)
                                  ("public final static int " + ca + " = " +
                                     n + ";")
                                }) mkString "\n"
                             m.group(1) + " class " + m.group(2) + "{\n" +
                             members + "\n}"
                           })
    (res, typeSubst.toMap, caseSubst.toMap)
  }

  def rewriteTypes(str : String, typeSubst : Map[String, String]) : String = {
    TypeUseDecl.replaceAllIn(str, {
                               m =>
                               (typeSubst get m.group(1)) match {
                                 case Some(newType) =>
                                   newType + " "
                                 case None =>
                                   m.matched
                               }
                             })
  }

  def rewriteCases(str : String, caseSubst : Map[String, String]) : String = {
    SwitchCase.replaceAllIn(str, {
                              m =>
                              (caseSubst get m.group(1)) match {
                                case Some(newCase) =>
                                  "case " + newCase + ":"
                                case None =>
                                  m.matched
                              }
                            })
  }

  def findJavaFiles(fs : Seq[File]) : Seq[File] =
    fs.flatMap(findJavaFiles)

  def findJavaFiles(f : File): Seq[File] =
    if (f.isFile && f.getName.endsWith(".java"))
      List(f)
    else if (f.isDirectory)
      findJavaFiles(f.listFiles)
    else
      List()

}
