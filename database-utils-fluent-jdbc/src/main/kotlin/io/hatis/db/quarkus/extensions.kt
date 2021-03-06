package io.hatis.db.quarkus

import io.hatis.db.DeleteBuilder
import io.hatis.db.InsertBuilder
import io.hatis.db.SelectBuilder
import io.hatis.db.UpdateBuilder
import org.codejargon.fluentjdbc.api.FluentJdbc
import org.codejargon.fluentjdbc.api.query.BatchQuery
import org.codejargon.fluentjdbc.api.query.SelectQuery
import org.codejargon.fluentjdbc.api.query.UpdateQuery
import org.codejargon.fluentjdbc.api.query.UpdateResult

private fun update(fluentJdbc: FluentJdbc, sqlAndParams: Pair<String, List<Any?>>): UpdateQuery {
    val (sql, params) = sqlAndParams

    return fluentJdbc.query()
        .update(sql)
        .namedParams(toNamedParams(params))
}

private fun insert(fluentJdbc: FluentJdbc, sqlAndParams: Pair<String, List<List<Any?>>>): BatchQuery {
    val (sql, params) = sqlAndParams

    return fluentJdbc.query()
        .batch(sql)
        .params(params)
}

private fun select(fluentJdbc: FluentJdbc, sqlAndParams: Pair<String, List<Any?>>): SelectQuery {
    val (sql, params) = sqlAndParams

    return fluentJdbc.query()
        .select(sql)
        .namedParams(toNamedParams(params))
}

private fun toNamedParams(params: List<Any?>) = params
    .mapIndexed { index, value -> "${index + 1}" to value }
    .toMap()

private fun paramPlaceholder(index: Int) = "?"
private fun paramPlaceholderNamed(index: Int) = ":$index"

fun UpdateBuilder.build(fluentJdbc: FluentJdbc) = update(fluentJdbc, buildSqlAndParams(::paramPlaceholderNamed))
fun UpdateBuilder.execute(fluentJdbc: FluentJdbc): UpdateResult = build(fluentJdbc).run()

fun InsertBuilder.build(fluentJdbc: FluentJdbc) = insert(fluentJdbc, buildSqlAndParams(::paramPlaceholder))
fun InsertBuilder.execute(fluentJdbc: FluentJdbc): Collection<UpdateResult> = build(fluentJdbc).run()

fun SelectBuilder.build(fluentJdbc: FluentJdbc) = select(fluentJdbc, buildSqlAndParams(::paramPlaceholderNamed))

fun DeleteBuilder.build(fluentJdbc: FluentJdbc) = update(fluentJdbc, buildSqlAndParams(::paramPlaceholderNamed))
fun DeleteBuilder.execute(fluentJdbc: FluentJdbc): UpdateResult = build(fluentJdbc).run()