package org.mutabilitydetector.casestudies.jodamoney;

import static org.mutabilitydetector.unittesting.MutabilityAssert.assertImmutable;
import net.ttsui.junit.rules.pending.PendingImplementation;
import net.ttsui.junit.rules.pending.PendingRule;

import org.joda.money.BigMoney;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyPrintContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;

public class TestJodaMoney {
    
    @Rule public MethodRule pendingRule = new PendingRule();

    
    @Test @PendingImplementation
    public void testorg_joda_money_format_MoneyPrintContext() {
        assertImmutable(MoneyPrintContext.class);
    }

    @Test
    public void testorg_joda_money_format_MoneyAmountStyle() {
        assertImmutable(MoneyAmountStyle.class);
    }

    @Test @PendingImplementation
    public void testorg_joda_money_format_MoneyFormatter() {
        assertImmutable(MoneyFormatter.class);
    }

    @Test @PendingImplementation
    public void testorg_joda_money_BigMoney() {
        assertImmutable(BigMoney.class);
    }

    @Test @PendingImplementation
    public void testorg_joda_money_Money() {
        assertImmutable(Money.class);
    }

}
