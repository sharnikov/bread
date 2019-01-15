import domain.Good

object DomainTest {

  val good = Good(id = 1, name = "Булка", category = "Хлеб", price = 123)
  val good2 = Good(id = 2, name = "НеБулка", category = "Хлеб", price = 124)
  val good3 = Good(id = 3, name = "Кекс", category = "Сладкое", price = 50)

  val goodsList = List(good, good2, good3)
}
