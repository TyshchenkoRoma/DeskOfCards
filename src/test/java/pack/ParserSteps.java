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

    private  final String BASE_URL = "https://deckofcardsapi.com/api/deck";
    private  final String SHUFFLING_URL = "/new/shuffle/?deck_count=";
    private  final String DRAW_URL = "/draw/?count=";

    private Response response;
    private String id;
    private int cardNumber;


    @Before
    public void setUp() {
        RestAssured.baseURI = BASE_URL;
    }

    @Given("I have new game with {string} decks")
    public void iHaveNewDescWithDecks(String number) {
        id = getDeck_id(SHUFFLING_URL + number);
    }

    @When("I drow {string} cards")
    public void iDrowCards(String times) {
        for (int i = 0; i < Integer.parseInt(times); i++) {
            response = getResponse("/" + id + DRAW_URL + "2");
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
        id = getDeck_id(SHUFFLING_URL +"1");
    }

    @When("User gets five cards")
    public void userGetsFiveCards() {
        response = RestAssured.given().when().get("/" + id + DRAW_URL +"5");
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
        final List<String> anotherCardList = getResponse("/" + id + DRAW_URL+"47")
                .body().jsonPath().getList("cards.code");

        Assert.assertTrue(isRepeated(fiveCardList, anotherCardList));
    }

    private String getDeck_id(String path) {
        return RestAssured.given().when().get(path).body().jsonPath().getString("deck_id");
    }

    private Response getResponse(String path) {
        return RestAssured.given().when().get(path);
    }

    private boolean isRepeated(List<String> fiveCardList, List<String> anotherCardList) {
        for (String card : fiveCardList) {
            if (anotherCardList.stream().anyMatch(s -> s.contains(card)))
                return false;
        }
        return true;
    }
}