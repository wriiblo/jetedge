package com.rainbowpunch.jtdg.integration;

import com.rainbowpunch.jtdg.core.limiters.primitive.IntegerLimiter;
import com.rainbowpunch.jtdg.core.limiters.primitive.StringLimiter;
import com.rainbowpunch.jtdg.spi.PojoGeneratorBuilder;
import com.rainbowpunch.jtdg.test.Pojos.Extra;
import com.rainbowpunch.jtdg.test.Pojos.Person;
import com.rainbowpunch.jtdg.test.Pojos.Superhero;
import com.rainbowpunch.jtdg.test.Pojos.Vehicle;

import org.junit.Ignore;
import org.junit.Test;

import static com.rainbowpunch.jtdg.test.Assertions.assertPojosShallowEqual;
import static com.rainbowpunch.jtdg.test.Pojos.Power.FLIGHT;
import static com.rainbowpunch.jtdg.test.Pojos.Power.SPIDER_SENSE;
import static com.rainbowpunch.jtdg.test.Pojos.Power.XRAY_VISION;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PojoGeneratorIntegrationTest {
    private static final int RANDOM_SEED = 42;

    @Test
    public void testGenerateEmptyPojo() {
        Extra generated = new PojoGeneratorBuilder<>(Extra.class)
                .build()
                .generatePojo();

        assertNotNull(generated);
    }

    @Test
    public void testGenerateBasicPojo() {
        Person generated = new PojoGeneratorBuilder<>(Person.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo();

        assertEquals(-1742253836, generated.getAge());
        assertEquals("h1<t\"c!>ya,f,0(TDja_(!DkOIfD[$", generated.getName());
    }

    @Test
    public void testGeneratePojoWithInheritedFields() {
        Superhero generated = new PojoGeneratorBuilder<>(Superhero.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo();

        assertEquals(681416186, generated.getAge());
        assertEquals("h1<t\"c!>ya,f,0(TDja_(!DkOIfD[$", generated.getName());

        // also verify that direct fields are picked up
        assertNotNull(generated.getSuperPowers());
        assertNotNull(generated.getArchNemesis());
    }

    @Test
    public void testGeneratePojoWithListField() {
        Superhero generated = new PojoGeneratorBuilder<>(Superhero.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo();

        assertEquals(
                asList(XRAY_VISION, FLIGHT, FLIGHT, SPIDER_SENSE, SPIDER_SENSE, SPIDER_SENSE),
                generated.getSuperPowers()
        );
    }

    @Ignore("nested POJO does not currently inherit random seed")
    @Test
    public void testGeneratePojoWithNestedPojo() {
        Person generated = new PojoGeneratorBuilder<>(Superhero.class)
                .andUseRandomSeed(RANDOM_SEED)
                .build()
                .generatePojo()
                .getArchNemesis();

        assertEquals(0, generated.getAge());
        assertEquals("", generated.getName());
    }

    @Test
    public void testPojoGeneratorBuilderClone() {
        PojoGeneratorBuilder<Person> baseGen = new PojoGeneratorBuilder<>(Person.class);

        Person generatedA = baseGen.clone().build().generatePojo();
        Person generatedB = baseGen.clone().build().generatePojo();

        assertPojosShallowEqual(generatedA, generatedB);
    }

    @Test
    public void testLimitFieldByName() {
        int expectedLength = 4;
        Person generated = new PojoGeneratorBuilder<>(Person.class)
                .andUseRandomSeed(RANDOM_SEED)
                .andLimitField("name", new StringLimiter(expectedLength))
                .build()
                .generatePojo();

        assertEquals(expectedLength, generated.getName().length());
    }

    @Test
    public void testLimitAllFieldsOfType() {
        int expectedRange = 10;
        Vehicle generated = new PojoGeneratorBuilder<>(Vehicle.class)
                .andUseRandomSeed(RANDOM_SEED)
                // FIXME IntegerLimiter only matches on Integer, not int
                .andLimitAllFieldsOf(new IntegerLimiter(expectedRange))
                .build()
                .generatePojo();

        assertTrue(expectedRange >= generated.getMaxSpeed());
        assertTrue(expectedRange >= generated.getNumWheels());
    }

    @Test
    public void testUseCustomAnalyzer() {
        Person generated;
        generated = new PojoGeneratorBuilder<>(Person.class)
                .andUseRandomSeed(RANDOM_SEED)
                // use a custom analyzer that only includes string attributes
                .andUseAnalyzer(classAttributes ->
                        classAttributes.getFields().stream()
                                .filter(f -> f.getType().is(String.class)))
                .build()
                .generatePojo();

        assertEquals("h1<t\"c!>ya,f,0(TDja_(!DkOIfD[$", generated.getName());
        assertEquals(0, generated.getAge());
    }
}
