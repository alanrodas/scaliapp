/** ********************************************************************************************
  * Scaliapp
  * Version 0.1
  *
  * The primary distribution site is
  *
  * http://scaliapp.alanrodas.com
  *
  * Copyright 2014 alanrodas
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
  * except in compliance with the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the
  * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
  * either express or implied. See the License for the specific language governing permissions
  * and limitations under the License.
  * *********************************************************************************************/
package com.alanrodas.scaliapp

/**
 * This package provides a set of implicit conversions between
 * ''String'' and a set of different types.
 *
 * This package provides conversions from ''String'' to
 * ''Int'', ''Float'', ''Double'', ''Byte'' and ''Boolean''.
 * The conversions are performed using the standard Java
 * ''valueOf'' methods for numeric types, thus, incorrect
 * string may produce a NumberFormatException.
 *
 * To avoid possible exceptions, conversions from ''String''
 * to ''Option[Int]'', ''Option[Float]'', ''Option[Double]'',
 * ''Option[Byte]'' and ''Option[Boolean]''.
 *
 */
package object implicits {

	/**
	 * Implicitly converts from String to Int
	 *
	 * @throws NumberFormatException if the string does not represent a valid Int.
	 */
	implicit def stringToInt(s : String) : Int = Integer.decode(s)

	/**
	 * Implicitly converts from String to Double
	 *
	 * @throws NumberFormatException if the string does not represent a valid Double.
	 */
	implicit def stringToDouble(s : String) : Double = java.lang.Double.valueOf(s)

	/**
	 * Implicitly converts from String to Float
	 *
	 * @throws NumberFormatException if the string does not represent a valid Float.
	 */
	implicit def stringToFloat(s : String) : Float = java.lang.Float.valueOf(s)

	/**
	 * Implicitly converts from String to Byte
	 *
	 * @throws NumberFormatException if the string does not represent a valid Byte.
	 */
	implicit def stringToByte(s : String) : Byte = java.lang.Byte.valueOf(s)

	/**
	 * Implicitly converts from String to Boolean
	 *
	 * This method return ''true'' if the string matches any of ''true'', ''t'',
	 * ''yes'', ''y'', ''on'' or ''1'', returns ''false'' otherwise.
	 */
	implicit def stringToBool(s : String) : Boolean = stringToSomeBool(s).fold(false){b=>b}

	/**
	 * Implicitly converts from String to Option[Int].
	 *
	 * Returns ''Some(x)'' if the string represents a valid Int ''x'', return ''None''
	 * otherwise.
	 */
	implicit def stringToSomeInt(s : String) : Option[Int] =
		try {Some(stringToInt(s))} catch {case nfe : NumberFormatException => None }

	/**
	 * Implicitly converts from String to Option[Double].
	 *
	 * Returns ''Some(x)'' if the string represents a valid Double ''x'', return ''None''
	 * otherwise.
	 */
	implicit def stringToSomeDouble(s : String) : Option[Double] =
		try {Some(stringToDouble(s))} catch {case nfe : NumberFormatException => None }

	/**
	 * Implicitly converts from String to Option[Float].
	 *
	 * Returns ''Some(x)'' if the string represents a valid Float ''x'', return ''None''
	 * otherwise.
	 */
	implicit def stringToSomeFloat(s : String) : Option[Float] =
		try {Some(stringToFloat(s))} catch {case nfe : NumberFormatException => None }

	/**
	 * Implicitly converts from String to Option[Byte].
	 *
	 * Returns ''Some(x)'' if the string represents a valid Byte ''x'', return ''None''
	 * otherwise.
	 */
	implicit def stringToSomeByte(s : String) : Option[Byte] =
		try {Some(stringToByte(s))} catch {case nfe : NumberFormatException => None }

	/**
	 * Implicitly converts from String to Option[Boolean]
	 *
	 * Returns ''Some(true)'' if the string matches any of ''true'', ''t'',
	 * ''yes'', ''y'', ''on'' or ''1'', returns ''Some(false)'' if matches
	 * ''false'', ''f'', ''no'', ''n'', ''off'' or ''0'' and ''None'' otherwise.
	 */
	implicit def stringToSomeBool(s : String) : Option[Boolean] = {
		if (Seq("true", "on", "yes", "t", "y", "1").contains(s.toLowerCase)) Some(true)
		else if (Seq("false", "off", "no", "f", "n", "0").contains(s.toLowerCase)) Some(false)
		else None
	}
}