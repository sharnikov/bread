import domain.Domain.Id
import domain._

object DomainTestData {

  val category = "cat"

  val userId: Id = 1
  val userId2: Id = 2

  val orderId: Id = 3
  val orderId2: Id = 4

  val good = Good(id = 1, name = "Булка", category = "Хлеб", price = 123)
  val good2 = Good(id = 2, name = "НеБулка", category = "Хлеб", price = 124)
  val good3 = Good(id = 3, name = "Кекс", category = "Сладкое", price = 50)

  val goodsList = List(good, good2, good3)

  val fullGoodPack = FullGoodPack(
    quantity = 2,
    good
  )

  val fullGoodPack2 = FullGoodPack(
    quantity = 3,
    good2
  )

  val fullOrder = FullOrder(
    userId = userId,
    id = orderId,
    packs = List(fullGoodPack, fullGoodPack2)
  )

  val goodPack = GoodsPack(
    quantity = 2,
    goodId = good.id
  )

  val goodPack2 = GoodsPack(
    quantity = 3,
    goodId = good2.id
  )

  val newOrder = NewOrder(
    userId = userId,
    packs = List(goodPack, goodPack2)
  )
}
