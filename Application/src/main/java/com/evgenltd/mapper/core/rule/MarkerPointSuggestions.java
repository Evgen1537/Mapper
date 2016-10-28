package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.MarkerPoint;
import math.geom2d.Point2D;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

/**
 * Project: mapper
 * Author:  Lebedev
 * Created: 05-07-2016 18:37
 */
@ParametersAreNonnullByDefault
public class MarkerPointSuggestions {

    public static Optional<MarkerPoint> getNearPoint(final Collection<MarkerPoint> sourcePointList, final double targetX, final double targetY)    {

        return sourcePointList
                .stream()
                .min((o1, o2) -> Double.compare(
                        Point2D.distance(o1.getX(), o1.getY(), targetX, targetY),
                        Point2D.distance(o2.getX(), o2.getY(), targetX, targetY)
                ));

    }

	public static Collection<MarkerPoint> getNeighbours(final Collection<MarkerPoint> sourcePointList, final Long targetPointOrderNumber)	{

		if(sourcePointList.isEmpty())	{
			return Collections.emptyList();
		}

		final Collection<MarkerPoint> result = new ArrayList<>();

		sourcePointList
				.stream()
				.filter(markerPoint -> markerPoint.getOrderNumber() < targetPointOrderNumber)
				.max(MarkerPoint.MARKER_POINT_COMPARATOR)
				.map(result::add)
				.orElseGet(() -> {
					sourcePointList
							.stream()
							.filter(markerPoint -> !Objects.equals(
									markerPoint.getOrderNumber(),
									targetPointOrderNumber
							))
							.max(MarkerPoint.MARKER_POINT_COMPARATOR)
							.ifPresent(result::add);
				   	return false;
				});

		sourcePointList.stream()
				.filter(markerPoint -> markerPoint.getOrderNumber() > targetPointOrderNumber)
				.min(MarkerPoint.MARKER_POINT_COMPARATOR)
				.map(result::add)
				.orElseGet(() -> {
					sourcePointList
							.stream()
							.filter(markerPoint -> !Objects.equals(
									markerPoint.getOrderNumber(),
									targetPointOrderNumber
							))
							.min(MarkerPoint.MARKER_POINT_COMPARATOR)
							.ifPresent(result::add);
					return false;
				});

		return result;

	}

}
