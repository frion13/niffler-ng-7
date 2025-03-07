package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.open;

@WebTest
public class FriendsWebTest {

    @User(
            friends = 1
    )

    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        final String friendUsername = user.testData().friendsUsernames()[0];
        open(LoginPage.URL, LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .toFriendsPage()
                .checkThatPageLoaded()
                .checkUserInUserFriendsList(friendUsername);
    }

    @User
    @Test
    void friendShouldBeEmptyForNewUser(UserJson user) {
        open(LoginPage.URL, LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .toFriendsPage()
                .checkThatPageLoaded()
                .checkFriendsIsEmpty();
    }

    @User(
            incomeInvitations = 2
    )
    @Test
    void incomeInvitationShouldBePresentInFriendsTable(UserJson user) {
        final String friendUsername = user.testData().incomeInvitationsUsernames()[0];
        open(LoginPage.URL, LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .toFriendsPage()
                .checkThatPageLoaded()
                .checkIncomeRequest(friendUsername);
    }

    @User(
            outcomeInvitations = 3
    )
    @Test
    void outcomeInvitationShouldBePresentInAllPeopleTable(UserJson user) {
        final String friendUsername = user.testData().outcomeInvitationsUsernames()[0];
        open(LoginPage.URL, LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .getHeader()
                .toAllPeoplesPage()
                .checkOutcomeRequest(friendUsername);
    }

    @User(incomeInvitations = 1)
    @Test
    void acceptInvitationTest(UserJson user) {
        final String userToAccept = user.testData().incomeInvitationsUsernames()[0];
        FriendsPage friendsPage = open(LoginPage.URL, LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .getHeader()
                .toFriendsPage()
                .checkExistingInvitationsCount(1)
                .acceptFriendInvitationFromUser(userToAccept)
                .checkExistingInvitationsCount(0);

        Selenide.refresh();

        friendsPage.checkExistingFriendsCount(1)
                .checkExistingFriends(userToAccept);
    }

    @User(incomeInvitations = 1)
    @Test
    void declineInvitationTest(UserJson user) {
        final String userToAccept = user.testData().incomeInvitationsUsernames()[0];
        FriendsPage friendsPage = open(LoginPage.URL, LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .getHeader()
                .toFriendsPage()
                .checkExistingInvitationsCount(1)
                .declineFriendInvitationFromUser(userToAccept)
                .checkExistingInvitationsCount(0);

        Selenide.refresh();

        friendsPage.checkFriendsIsEmpty();
    }
}