package com.kasirpintar.automation.steps;

import com.kasirpintar.automation.context.ScenarioContext;
import com.kasirpintar.automation.model.Barang;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Step definitions for the Default-product CRUD scenarios. Navigation into and
 * around the product area; login lives in {@link LoginSteps}. Shared page state
 * comes from the injected {@link ScenarioContext}.
 */
public class BarangSteps {

    private final ScenarioContext ctx;

    public BarangSteps(ScenarioContext ctx) {
        this.ctx = ctx;
    }

    @Given("the user opens the product list page")
    public void userOpensProductList() {
        ctx.managementPage = ctx.salesHome.openManagement();
        assertTrue(ctx.managementPage.isAt(), "Management page did not open");
        ctx.listPage = ctx.managementPage.openProductOrService();
        assertTrue(ctx.listPage.isAt(), "Product list page did not open");
    }

    @Given("a Default product named {string} exists")
    public void defaultProductExists(String name) {
        if (!ctx.listPage.hasItem(name)) {
            ctx.formPage = ctx.listPage.tapAdd();
            ctx.listPage = ctx.formPage.createDefaultBarang(Barang.defaultItem(name, "10000", "10"));
        }
        assertTrue(ctx.listPage.hasItem(name), "Precondition failed: product '" + name + "' is missing");
    }

    @When("the user adds a Default product named {string} with price {string} and stock {string}")
    public void userAddsDefaultProduct(String name, String price, String stock) {
        ctx.formPage = ctx.listPage.tapAdd();
        assertEquals("Default", ctx.formPage.selectedItemType(),
                "The default item type should be 'Default'");
        ctx.listPage = ctx.formPage.createDefaultBarang(Barang.defaultItem(name, price, stock));
    }

    @When("the user opens the detail of product {string}")
    public void userOpensProductDetail(String name) {
        ctx.detailPage = ctx.listPage.openItem(name);
        assertTrue(ctx.detailPage.isAt(), "Product detail page did not open");
    }

    @When("the user changes the name of product {string} to {string}")
    public void userChangesProductName(String oldName, String newName) {
        ctx.detailPage = ctx.listPage.openItem(oldName);
        ctx.formPage = ctx.detailPage.tapEdit();
        ctx.listPage = ctx.formPage.updateName(newName);
    }


    @When("the user deletes product {string}")
    public void userDeletesProduct(String name) {
        ctx.detailPage = ctx.listPage.openItem(name);
        ctx.listPage = ctx.detailPage.delete();
    }


    @Then("product {string} appears in the product list")
    public void productAppearsInList(String name) {
        assertTrue(ctx.listPage.hasItem(name),
                "Product '" + name + "' was not found in the list");
    }

    @Then("the product detail shows the name {string}")
    public void productDetailShowsName(String name) {
        assertEquals(name, ctx.detailPage.readName(), "Product detail name does not match");
    }

    @Then("product {string} does not appear in the product list")
    public void productDoesNotAppearInList(String name) {
        assertFalse(ctx.listPage.hasItem(name),
                "Product '" + name + "' still exists after deletion");
    }
}
