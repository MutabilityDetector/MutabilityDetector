package org.mutabilitydetector.casestudies.jodamoney;

import org.junit.Test;
import org.mutabilitydetector.unittesting.MutabilityAssert;

public class TestJodaMoney {
	
	@Test
	public void testLocale() throws Exception {
		MutabilityAssert.assertImmutable(java.util.Locale.class);
	}
	
    @Test
    public void testorg_joda_money_format_MoneyPrintContext() {
        MutabilityAssert.assertImmutable(org.joda.money.format.MoneyPrintContext.class);
    }

    @Test
    public void testorg_joda_money_format_MoneyAmountStyle() {
        MutabilityAssert.assertImmutable(org.joda.money.format.MoneyAmountStyle.class);
    }

    @Test
    public void testorg_joda_money_format_MoneyFormatter() {
        MutabilityAssert.assertImmutable(org.joda.money.format.MoneyFormatter.class);
    }

    @Test
    public void testorg_joda_money_BigMoney() {
        MutabilityAssert.assertImmutable(org.joda.money.BigMoney.class);
    }

    @Test
    public void testorg_joda_money_Money() {
        MutabilityAssert.assertImmutable(org.joda.money.Money.class);
    }

}
