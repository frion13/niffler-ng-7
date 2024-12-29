package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;

@ExtendWith(BrowserExtension.class)
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIENDS) StaticUser user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .checkIsLoaded()
                .openFriendsPage()
                .checkUserInUserFriendsList(user.friends());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .checkIsLoaded()
                .openFriendsPage()
                .checkFriendsIsEmpty();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void incomeInvitationShouldBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .checkIsLoaded()
                .openFriendsPage()
                .checkIncomeRequest(user.incomeRequest());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void outcomeInvitationShouldBePresentInAllPeopleTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user){
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password())
                .checkIsLoaded()
                .openAllPeoplesPage()
                .checkOutcomeRequest(user.outcomeRequest());
    }
}