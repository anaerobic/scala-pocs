package com.app

import org.apache.log4j.Logger

trait J2SELogging { protected val log = Logger.getLogger(getClass.getName) }