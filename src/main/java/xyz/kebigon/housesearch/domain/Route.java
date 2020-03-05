package xyz.kebigon.housesearch.domain;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Route
{
    /**
     * the station of departure
     */
    private String from;

    /**
     * the station of arrival
     */
    private String to;

    /**
     * time in minutes between the two stations
     */
    private int time;

    /**
     * price in yens between the two stations
     */
    private int fare;

    /**
     * number of transfers between the two stations
     */
    private int transfer;

    /**
     * Time > Fare > Transfer comparator
     */
    public static final Comparator<Route> TIME_COMPARATOR = new Comparator<Route>()
    {
        @Override
        public int compare(Route route1, Route route2)
        {
            int result;
            if ((result = Integer.compare(route1.time, route2.time)) != 0)
                return result;
            else if ((result = Integer.compare(route1.fare, route2.fare)) != 0)
                return result;
            else
                return Integer.compare(route1.transfer, route2.transfer);
        }
    };

    /**
     * Fare > Transfer > Time comparator
     */
    public static final Comparator<Route> FARE_COMPARATOR = new Comparator<Route>()
    {
        @Override
        public int compare(Route route1, Route route2)
        {
            int result;
            if ((result = Integer.compare(route1.fare, route2.fare)) != 0)
                return result;
            else if ((result = Integer.compare(route1.transfer, route2.transfer)) != 0)
                return result;
            else
                return Integer.compare(route1.time, route2.time);
        }
    };

    /**
     * Transfer > Time > Fare comparator
     */
    public static final Comparator<Route> TRANSFER_COMPARATOR = new Comparator<Route>()
    {
        @Override
        public int compare(Route route1, Route route2)
        {
            int result;
            if ((result = Integer.compare(route1.transfer, route2.transfer)) != 0)
                return result;
            else if ((result = Integer.compare(route1.time, route2.time)) != 0)
                return result;
            else
                return Integer.compare(route1.fare, route2.fare);
        }
    };

    /**
     * Route to be used when no routes are found
     */
    public static final Route IMPOSSIBLE_ROUTE = new Route(null, null, 9999, 9999, 9999);
}
