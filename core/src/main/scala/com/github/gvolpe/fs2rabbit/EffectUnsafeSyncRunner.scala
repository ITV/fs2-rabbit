/*
 * Copyright 2017 Fs2 Rabbit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.gvolpe.fs2rabbit

/**
  * A generic unsafe synchronously runner representation for any [[cats.effect.Effect]].
  *
  * The reasons behind having this type is just to use it only once in your program as
  * part of generic solution for any effect. Some types already implement this method.
  *
  * For example the [[cats.effect.IO.unsafeRunSync()]].
  *
  * When having a Stream defined as your whole program, at the "end of the universe" you
  * will do this for example:
  *
  * {{{
  * import cats.effect.IO
  * import fs2._
  *
  * val program = Stream.eval(IO { println("Hey!") })
  *
  * program.run.unsafeRunSync
  * }}}
  *
  * Having this generic type allows the fs2-rabbit library to do this for any effect:
  *
  * {{{
  * import cats.effect.Effect
  * import fs2._
  *
  * def createProgram[F[_]](implicit F: Effect[F], R: EffectUnsafeSyncRunner[F]) = {
  *   Stream.eval(F.delay { println("hey") })
  * }
  *
  * def run[F[_]](program: Stream[F, Unit])(implicit F: Effect[F], R: EffectUnsafeSyncRunner[F]) =
  *   R.unsafeRunSync(program.run)
  *
  * run[IO](createProgram[IO])
  * }}}
  *
  * Its only intention is to be used together with [[StreamLoop]]
  * */
trait EffectUnsafeSyncRunner[F[_]] {
  /**
    * Produces the result by running the encapsulated effects as impure
    * side effects.
    * */
  def unsafeRunSync(effect: F[Unit]): Unit
}

object EffectUnsafeSyncRunner {
  def apply[F[_] : EffectUnsafeSyncRunner]: EffectUnsafeSyncRunner[F] = implicitly[EffectUnsafeSyncRunner[F]]
}
