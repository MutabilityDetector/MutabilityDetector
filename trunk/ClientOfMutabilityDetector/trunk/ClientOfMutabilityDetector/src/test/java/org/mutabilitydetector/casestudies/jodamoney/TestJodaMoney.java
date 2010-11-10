package org.mutabilitydetector.casestudies.jodamoney;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;

import java.util.Locale;

import org.joda.money.BigMoney;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyPrintContext;
import org.junit.Test;

public class TestJodaMoney {
	
	@Test
	public void testLocale() throws Exception {
		assertImmutable(Locale.class);
	}
	
    @Test
    public void testorg_joda_money_format_MoneyPrintContext() {
        assertImmutable(MoneyPrintContext.class);
    }

    @Test
    public void testorg_joda_money_format_MoneyAmountStyle() {
        assertImmutable(MoneyAmountStyle.class);
    }

    @Test
    public void testorg_joda_money_format_MoneyFormatter() {
        assertImmutable(MoneyFormatter.class);
    }

    @Test
    public void testorg_joda_money_BigMoney() {
        assertImmutable(BigMoney.class);
    }

    @Test
    public void testorg_joda_money_Money() {
        assertImmutable(Money.class);
    }

}
