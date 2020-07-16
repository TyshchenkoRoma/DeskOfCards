package folder;

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
        response = RestAssured.given().when().get("/new/shuffle/?deck_count=" + number);
        id = response.body().jsonPath().getString("deck_id");
    }

    @When("I drow {string} cards")
    public void iDrowCards(String times) {
        for (int i = 0; i < Integer.parseInt(times); i++) {
            response = RestAssured.given().when().get("/" + id + "/draw/?count=2");
        }
        cardNumber = response.body().jsonPath().getInt("remaining");
    }

    @Then("card count in deck is changed to {string}")
    public void cardCountInDeckIsChangedTo(String arg0) {
        int expectedCount = Integer.parseInt(arg0);

        Assert.assertEquals(cardNumber, expectedCount);
    }

    @Given("User has deck with Aceses only")
    public void userHaveDeckDescWithAcesesOnly() {
        response = RestAssured.given().when().get("/new/shuffle/?cards=AS,AD,AC,AH");
        id = response.body().jsonPath().getString("deck_id");
    }

    @Then("User can get Aceses only")
    public void userCanGetAcesesOnly() {
        response = RestAssured.given().when().get(id + "/draw/?count=4");
        final List<String> acesesList = response.body().jsonPath().getList("cards.code");

        Assert.assertTrue(acesesList.stream().allMatch(s -> s.contains("A")));
    }

    @Given("User has new game with one deck")
    public void userHasNewGameWithOneDeck() {
        response = RestAssured.given().when().get("/new/shuffle/?deck_count=1");
        id = response.body().jsonPath().getString("deck_id");
    }

    @When("User gets five cards")
    public void userGetsFiveCards() {
        response = RestAssured.given().when().get("/" + id + "/draw/?count=5");
    }

    @Then("card amound was changed")
    public void cardAmoundWasChanged() {
        int actualAmound = response.body().jsonPath().getInt("remaining");
        int expectedAmound = 47;

        Assert.assertEquals(expectedAmound, actualAmound);
    }

    @Then("cards not repeat in the desk after drawning")
    public void cardsNotRepeatInTheDeskAfterDrawning() {
        final List<String> fiveCardList = response.body().jsonPath().getList("cards.code");
        final List<String> anotherCardList = RestAssured.given().when().get("/" + id + "/draw/?count=47")
                .body().jsonPath().getList("cards.code");

        boolean isRepeated = false;
        for (String card : fiveCardList) {
          if (anotherCardList.stream().anyMatch((s -> s.contains(card))) == true) {
              isRepeated = true;
          }
        }
        Assert.assertFalse(isRepeated);
    }
}