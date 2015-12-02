package scala

import DunceCap.attr.Attr
import net.liftweb.json.DefaultFormats
import net.liftweb.json._
import scala.io._
import net.liftweb.json.Serialization.{read, write, writePretty}

case class QueryPlans(val queryPlans:List[QueryPlan])

case class QueryPlan(val query_type:String,
                val relations:List[QueryPlanRelationInfo],
                val output:QueryPlanOutputInfo,
                val ghd:List[QueryPlanBagInfo],
                val topdown:List[TopDownPassIterator]) {

  override def toString(): String = {
    implicit val formats = DefaultFormats
    writePretty(this)
  }

  def toJSON(): Unit = {
    val filename = "query.json"
    implicit val formats = Serialization.formats(NoTypeHints)
    scala.tools.nsc.io.File(filename).writeAll(writePretty(this))
  }
}

object QP {
  def fromJSON(filename:String) : QueryPlans = {
    val fileContents = Source.fromFile(filename).getLines.mkString
    implicit val formats = DefaultFormats
    parse(fileContents).extract[QueryPlans]
  }
}

case class QueryPlanRelationInfo(val name:String,
                            val ordering:List[Int],
                            val attributes:Option[List[List[Attr]]],
                            val annotation:String)

case class QueryPlanOutputInfo(val name:String,
                          val ordering:List[Int],
                          val annotation:String)

case class QueryPlanBagInfo(val name:String,
                            val duplicateOf:Option[String],
                            val attributes:List[Attr],
                            val annotation:String,
                            val relations:List[QueryPlanRelationInfo],
                            val nprr:List[QueryPlanAttrInfo],
                            val recursion:Option[QueryPlanRecursion])

case class QueryPlanAttrInfo(val name:String,
                        val accessors:List[QueryPlanAccessor],
                        val materialize:Boolean,
                        val selection:List[QueryPlanSelection],
                        val annotation:Option[Attr],
                        val aggregation:Option[QueryPlanAggregation],
                        /* The last two here are never filled out in the top down pass*/
                        val prevMaterialized:Option[Attr],
                        val nextMaterialized:Option[Attr])

case class QueryPlanRecursion(
                val input:String,
                val criteria:String,
                val converganceValue:String)

case class QueryPlanAggregation(val operation:String,
                           val init:String,
                           val expression:String,
                           val prev:Option[Attr],
                           val next:Option[Attr])

case class QueryPlanSelection(val operation:String,
                              val expression:String)

case class QueryPlanAccessor(val name:String,
                        val attrs:List[Attr],
                        val annotated:Boolean)

case class TopDownPassIterator(val iterator:String, /* bag name */
                               val attributeInfo:List[QueryPlanAttrInfo])
