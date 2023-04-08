package io.github.samuelc92.booking

package object entities {
  opaque type Name = String

  object Name:
    def apply(s: String): Name = s
}
