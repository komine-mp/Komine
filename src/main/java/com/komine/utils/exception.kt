package com.komine.utils

import java.io.PrintWriter
import java.io.StringWriter

fun exceptionMessage(throwable: Throwable) = StringWriter().also {
	throwable.printStackTrace(PrintWriter(it))
}.toString()
