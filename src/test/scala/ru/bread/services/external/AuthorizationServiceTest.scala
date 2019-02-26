package ru.bread.services.external

import ru.bread.database.services.UserDAO
import ru.bread.services.internal.FixedTimeProvider
import ru.bread.settings.config.TestSettings
import utils.TestStuff
import test.data.CommonTestData._

class AuthorizationServiceTest extends TestStuff with TestSettings {

  trait mocks {
    val userDAO = stub[UserDAO]
    val timeProvider = new FixedTimeProvider(time)

    (session.size _).when().returns(30)

  }

}
