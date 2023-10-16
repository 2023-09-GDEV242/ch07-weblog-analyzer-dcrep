/**
 * Read web server data and analyse hourly access patterns.
 * 
 * @author Daniel Corritore, modified from existing code from:
 *         David J. Barnes and Michael Kölling.
 * @version    2023.10.15
 */
public class LogAnalyzer
{
    // Where to calculate the hourly access counts.
    private int[] hourCounts;
    private int[] monthCounts;
    // Use a LogfileReader to access the data.
    private LogfileReader reader;

    /**
     * Create an object to analyze hourly web accesses.
     */
    public LogAnalyzer()
    { 
        this("demo.log");
    }
    /**
     * Create an object to analyze hourly web accesses
     * @logfileName Name of the file to analyze
     */
    public LogAnalyzer(String logfileName)
    {
        // array of hourly access counts
        hourCounts = new int[24];
        // months as well
        monthCounts = new int[12];
        // Create the reader to obtain the data.
        reader = new LogfileReader(logfileName);
    }

    /**
     * Analyze the hourly access data from the log file.
     */
    public void analyzeHourlyData()
    {
        // In case a previous analysis was run, reset to start
        reader.reset();
        while(reader.hasNext()) {
            LogEntry entry = reader.next();
            int hour = entry.getHour();
            hourCounts[hour]++;
        }
    }
    
    /**
     * Analyze the monthly access data from the log file.
     */
    public void analyzeMonthlyData()
    {
        // In case a previous analysis was run, reset to start
        reader.reset();
        while(reader.hasNext()) {
            // -1 is important, converting 1-12 to 0-11
            monthCounts[reader.next().getMonth() - 1]++;
        }
    }

    /**
    * Return the number of accesses recorded in the log file.
     */
    public int numberOfAccesses()
    {
        int total = 0;
        // Add the value in each element of hourCounts to total.
        for (int i=0; i < hourCounts.length; i++) {
            total += hourCounts[i];
        }
        return total;
    }
    
    /**
     * Return the busiest hour according to the log file
     */
    public int busiestHour()
    {
        int busiestHour = 0;
        int busiestHourAccesses = 0;
        for (int i=0; i < hourCounts.length; i++) {
            int accesses = hourCounts[i];
            // This hour's accesses > previously recorded hour?
            if (accesses > busiestHourAccesses) {
                // adjust values to reflect this hour and it's accesses
                busiestHour = i;
                busiestHourAccesses = accesses;
            }            
        }
        return busiestHour;
    }
    
    /**
     * Return the quietest hour according to the log file.
     */
    public int quietestHour()
    {
        // Invalid hour if there's something missing
        int quietestHour = -1;
        // Start with the largest possible value
        int quietestHourAccesses = Integer.MAX_VALUE;
        for (int i=0; i < hourCounts.length; i++) {
            int accesses = hourCounts[i];
            // This hour's accesses not 0 AND < previously recorded hour?
            if (accesses != 0 && accesses < quietestHourAccesses) {
                // adjust values to reflect this hour and it's accesses
                quietestHour = i;
                quietestHourAccesses = accesses;
            }
        }
        return quietestHour;
    }

    /**
     * Return the busiest 2-hour time span according to the log file
     */
    public int busiestTwoHour()
    {
        int busiestTwoHourStart = 0;
        int busiestTwoHourAccesses = 0;
        for (int i=0; i < hourCounts.length; i++) {
            int accesses = hourCounts[i];
            // Add next hour's accesses, but check for special case
            // at 23 hours (11pm)
            if (i == hourCounts.length - 1) { // cycle around to [0] (12am)
                accesses += hourCounts[0];
            }
            else {  // everything up to 22 hours, add next indexed hour's values
                accesses += hourCounts[i + 1];
            }
            
            // Two-hours' accesses > previously recorded two-hour span?
            if (accesses > busiestTwoHourAccesses) {
                // adjust values to reflect this hour and it's accesses
                busiestTwoHourStart = i;
                busiestTwoHourAccesses = accesses;
            }            
        }
        return busiestTwoHourStart;
    }
    
    /**
     * Return the busiest month according to the log file
     */
    public int busiestMonth()
    {
        int busiestMonth = 0;
        int busiestMonthAccesses = 0;
        for (int i=0; i < monthCounts.length; i++) {
            int accesses = monthCounts[i];
            // This month's accesses > previously recorded month?
            if (accesses > busiestMonthAccesses) {
                // adjust values to reflect this month and it's accesses
                busiestMonth = i;
                busiestMonthAccesses = accesses;
            }            
        }
        // 0 to 1 base
        return (busiestMonth + 1);
    }
    /**
     * Return the quietest month according to the log file.
     */
    public int quietestMonth()
    {
        // Invalid month if there's something missing
        int quietestMonth = -1;
        // Start with the largest possible value
        int quietestMonthAccesses = Integer.MAX_VALUE;
        for (int i=0; i < monthCounts.length; i++) {
            int accesses = monthCounts[i];
            // This month's accesses not 0 AND < previously recorded month?
            if (accesses != 0 && accesses < quietestMonthAccesses) {
                // adjust values to reflect this month and it's accesses
                quietestMonth = i;
                quietestMonthAccesses = accesses;
            }
        }
        // 0 to 1 base
        return (quietestMonth + 1);
    }
    /**
    * Return an average of accesses per month as recorded in the log file.
     */
    public int averageAccessesPerMonth()
    {
        int total = 0;
        // Add the value in each element of monthCounts to total.        
        for (int i=0; i < monthCounts.length; i++) {
            // Debug
            //System.out.println("Month " + (i+1) + " hours:" + monthCounts[i]);
            total += monthCounts[i];
        }
        // This is actually a constant given numberOfAccesses()/12
        return (total / monthCounts.length);
    }

    /**
     * Print the hourly counts.
     * These should have been set with a prior
     * call to analyzeHourlyData.
     */
    public void printHourlyCounts()
    {
        System.out.println("Hr: Count");
        for(int hour = 0; hour < hourCounts.length; hour++) {
            System.out.println(hour + ": " + hourCounts[hour]);
        }
    }
    
    /**
     * Print the monthly counts.
     * These should have been set with a prior
     * call to analyzeMonthlyData.
     */
    public void printMonthlyCounts()
    {
        System.out.println("Month: Count");
        for(int month = 0; month < monthCounts.length; month++) {
            System.out.println("" + (month + 1) + ": " + monthCounts[month]);
        }
    }

    /**
     * Print the lines of data read by the LogfileReader
     */
    public void printData()
    {
        reader.printData();
    }
}
