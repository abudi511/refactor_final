package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    // change public to private
    private static Map<String, Play> plays;
    private Invoice invoice;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;
        // put private for result frmt and play
        final StringBuilder result = new StringBuilder("Statement for " + invoice.getCustomer()
                + System.lineSeparator());

        for (Performance performance : invoice.getPerformances()) {
            final Play getPlay = plays.get(performance.getPlayID());
            final int rslt = getAmount(performance);

            // add volume credits
            volumeCredits += getVolumeCredits(performance, getPlay);

            // print line for this order
            // magic number 100 change to Percent_factor
            result.append(String.format("  %s: %s (%s seats)%n", getPlay.getName(),
                    usd(getAmount(performance)), performance.getAudience()));
            totalAmount += rslt;
        }
        // magic number 100 change to percent factor
        result.append(String.format("Amount owed is %s%n", usd(totalAmount)));
        result.append(String.format("You earned %s credits%n", volumeCredits));
        return result.toString();
    }

    private static String usd(int totalAmount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(totalAmount / Constants.PERCENT_FACTOR);
    }

    private static int getVolumeCredits(Performance performance, Play getPlay) {
        int result = Math.max(performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
        // add extra credit for every five comedy attendees
        // add braces
        if ("comedy".equals(getPlay.getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    /**
     * Returns the play's Type given the performance.
     * @param performance the performance
     * @return the play Type
     */
    public static String getPlay(Performance performance) {
        return plays.get(performance.getPlayID()).getType();
    }

    private static int getAmount(Performance performance) {
        int thisAmount = 0;
        switch (getPlay(performance)) {
            case "tragedy":
                // change 40000 to TRAGEDY_BASE_AMOUNT
                thisAmount = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    // change 1000 to TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON . change 30 to Threshold
                    thisAmount += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                thisAmount = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    thisAmount += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience() - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                thisAmount += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", getPlay(performance)));
        }
        return thisAmount;
    }
}
