package xyz.kebigon.housesearch.domain;

import lombok.Data;

@Data
public class Posting
{
	/**
	 * URL
	 */
	private final String url;

	/**
	 * Price (in yens)
	 */
	private final Long price;

	/**
	 * Age (in years)
	 */
	private final Integer age;

	/**
	 * Land surface (in square meters)
	 */
	private final Double landSurface;

	/**
	 * House surface (in square meters)
	 */
	private final Double houseSurface;

	/**
	 * Time to go to the closest station (in minutes)
	 */
	private final Integer walkTimeToStation;

	/**
	 * Closest station
	 */
	private final String station;

	/**
	 * Fastest route in the ones compared by spring expression
	 */
	private Route fastestRoute;

	/**
	 * Cheapest route in the ones compared by spring expression
	 */
	private Route cheapestRoute;

	/**
	 * Easiest route in the ones compared by spring expression
	 */
	private Route easiestRoute;

	public void updateRoutes(Route route)
	{
		if (fastestRoute == null || route.getTime() < fastestRoute.getTime() || (route.getTime() == fastestRoute.getTime()
				&& (route.getFare() < fastestRoute.getFare() || route.getTransfer() < fastestRoute.getTransfer())))
			fastestRoute = route;
		if (cheapestRoute == null || route.getFare() < cheapestRoute.getFare() || (route.getFare() == cheapestRoute.getFare()
				&& (route.getTime() < cheapestRoute.getTime() || route.getTransfer() < cheapestRoute.getTransfer())))
			cheapestRoute = route;
		if (easiestRoute == null || route.getTransfer() < easiestRoute.getTransfer() || (route.getTransfer() == easiestRoute.getTransfer()
				&& (route.getTime() < easiestRoute.getTime() || route.getFare() < easiestRoute.getFare())))
			easiestRoute = route;
	}
}
