package pack;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;

import java.util.List;

public class ParserSteps {

    private Response response;
    private String id;
    private int cardNumber;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://deckofcardsapi.com/api/deck";
    }

    @Given("I have new game with {string} decks")
    public void iHaveNewDescWithDecks(String number) {
        id = getDeck_id("/new/shuffle/?deck_count=" + number);
    }

    @When("I drow {string} cards")
    public void iDrowCards(String times) {
        for (int i = 0; i < Integer.parseInt(times); i++) {
            response = getResponse("/" + id + "/draw/?count=2");
        }
        cardNumber = response.body().jsonPath().getInt("remaining");
    }

    @Then("card count in deck is changed to {string}")
    public void cardCountInDeckIsChangedTo(String arg0) {
        int expectedCount = Integer.parseInt(arg0);

        Assert.assertEquals(cardNumber, expectedCount);
    }

    @Given("User has deck with aces only")
    public void userHaveDeckDescWithAcesOnly() {
        id = getDeck_id("/new/shuffle/?cards=AS,AD,AC,AH");
    }

    @Then("User can get aces only")
    public void userCanGetAcesOnly() {
        response = getResponse(id + "/draw/?count=4");
        final List<String> acesesList = response.body().jsonPath().getList("cards.code");

        Assert.assertTrue(acesesList.stream().allMatch(s -> s.contains("A")));
    }

    @Given("User has new game with one deck")
    public void userHasNewGameWithOneDeck() {
        id = getDeck_id("/new/shuffle/?deck_count=1");
    }

    @When("User gets five cards")
    public void userGetsFiveCards() {
        response = RestAssured.given().when().get("/" + id + "/draw/?count=5");
    }

    @Then("card amount was changed")
    public void cardAmoundWasChanged() {
        int actualAmound = response.body().jsonPath().getInt("remaining");
        int expectedAmound = 47;

        Assert.assertEquals(expectedAmound, actualAmound);
    }

    @Then("cards not repeat in the desk after drawning")
    public void cardsNotRepeatInTheDeskAfterDrawning() {
        final List<String> fiveCardList = response.body().jsonPath().getList("cards.code");
        final List<String> anotherCardList = getResponse("/" + id + "/draw/?count=47")
                .body().jsonPath().getList("cards.code");

        boolean isRepeated = true;
        for (String card : fiveCardList) {
          if (anotherCardList.stream().anyMatch(s -> s.contains(card))) {
              isRepeated = false;
          }
        }

        Assert.assertTrue(isRepeated);
    }

    private static String getDeck_id(String path) {
        return RestAssured.given().when().get(path).body().jsonPath().getString("deck_id");
    }

    private static Response getResponse(String  path) {
        return RestAssured.given().when().get(path);
    }
}