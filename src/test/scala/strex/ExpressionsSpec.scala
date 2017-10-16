package strex

import org.scalatest._

import strex.Expression._
import strex.Evaluator._

class ExpressionsSpec extends FlatSpec with Matchers {
  "matcher" should "not find some text" in {
    val expression: Expression = 'X'
    val result: MatchingResult = (expression firstIn "abcdefg")
    result.found shouldBe false
  }

  it should "find first sequence position" in {
    val result: MatchingResult = ("cd" firstIn "acbcdefgccdsd")
    result.found shouldBe true
    result.position shouldBe 3
  }

  it should "find a string with regex reserved chars" in {
    val reservedChars = "<([{^-=$!|]})?*+.>"
    (reservedChars firstIn s"Tanto $reservedChars tempo").position shouldBe 6
  }

  it should "find first charater in 0-9 range" in {
    val expression: Expression = charInRange('0', '9')
    val text: String = (expression firstIn "ga 42.55")
    text shouldBe "4"
  }

  it should "find first number" in {
    (oneOrMore(digit) firstIn "fhd 441jj 713").text shouldBe "441"
  }

  it should "find a char or anohter" in {
    (("a" or "b") firstIn "7tb3a").position shouldBe 2
  }

  it should "find zero o more 'x' followed by an 'a'" in {
    (zeroOrMore("x") followedBy "a" firstIn ("4dxax31x")).position shouldBe 2
  }

  it should "not find zero o more 'x' followed by an 'c'" in {
    (zeroOrMore("x") followedBy "c" firstIn ("4dxax31x")).position shouldBe -1
  }

  it should "not find zero o more 'x' followed by an 'c' (only c is found)" in {
    (zeroOrMore("x") followedBy "c" firstIn ("ee4dcx31x")).text shouldBe "c"
  }

  it should "find hex digits" in {
    (oneOrMore(digit or charInRange('a', 'f') or charInRange('A', 'F')) firstIn "-k*-1f03a4**+zza13fb++").text shouldBe "1f03a4"
  }

  it should "find hex number" in {
    (oneOrMore(hexDigit) firstIn "y%&A0C3uu1aa").text shouldBe "A0C3"
  }

  it should "find something with a prefix" in {
    (oneOrMore("0x" followedBy oneOrMore(hexDigit) followedBy ";") firstIn "Test 0xffa;0x38b7;0xA103; (ok)").text shouldBe "0xffa;0x38b7;0xA103;"
  }

  it should "find first tab" in {
    (whitespace firstIn "Tanto\t  \t tempo fa").text shouldBe "\t"
  }

  it should "find first space" in {
    (whitespace firstIn "Tanto \t tempo fa").position shouldBe 5
  }

  it should "find first spacing" in {
    (spacing firstIn "Tanto\t  \n\t tempo fa").text shouldBe "\t  \n\t "
  }

  it should "find something with an optional part" in {
    ((optional(oneOrMore("a") or "zzz") followedBy "b") firstIn "313a..aabiib").text shouldBe "aab"
  }

  it should "find something with an optional part (2)" in {
    ((optional(oneOrMore("a") or "zzz") followedBy "b") firstIn "313a..zzzbbiib").text shouldBe "zzzb"
  }

  it should "find a sequence of hex constants separed by ," in {
    val expression = sequence(hexConstant, separator(","))
    (expression firstIn "casd0xffa, 0x38b7 ,0xA103--").text shouldBe "0xffa, 0x38b7 ,0xA103"
  }

  it should "find a sequence of numbers separed by &" in {
    val expression = sequence(number, separator('&'))
    (expression firstIn "hasd 3231 & 33 & 21 hasjdah").text shouldBe "3231 & 33 & 21"
  }

  // fixme remove. Regex support will be added later.
  it should "find a regex match" in {
    (regExpr("\\d+((\\s+)?&(\\s+)?\\d+)*") firstIn "hasd 3231 & 33 & 21 hasjdah").text shouldBe "3231 & 33 & 21"
  }

  it should "find an email address" in {
    (email firstIn "dashfds:fe--:nome@dominio.com:--,").text shouldBe "nome@dominio.com"
  }

  it should "find some words" in {
    val found = (letter followedBy oneOrMore(whitespace or letter) firstIn "      16344244 K total  memory").text
    found shouldBe "K total  memory"
  }

  it should "find a word" in {
    (word firstIn "***\\//test21[][[[second\\\\").text shouldBe "test21"
  }

  it should "find two sequences of world separed by ','" in {
    val expr = sequence(word, spacing) followedBy separator(',') followedBy sequence(word, spacing)
    val foundText: String = expr firstIn "::::zero  one01, two tre==777first, second**"
    foundText shouldBe "zero  one01, two tre"
  }

  // words
}
