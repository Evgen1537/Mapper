package com.evgenltd.mapper.core.rule;

import com.evgenltd.mapper.core.entity.Layer;
import math.geom2d.Point2D;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Project: mapper
 * Author:  Evgeniy
 * Created: 27-07-2016 00:29
 */
@ParametersAreNonnullByDefault
public class LayerMatcher {

	/**
	 * Performs matching target layers with source layers and also with each other.
	 * Result of invocation is a collection of matching groups.
	 * Each group contains layers which can be interpreted as one combined layer
	 * Group can contains single layer - it means that layer does not have matches with all other layers
	 * @param targetLayerList - layers for matching, offset can be changed during invocation
	 * @param sourceLayerList - source layers, assume that they already matched with each other,
	 *                           otherwise result of the invocation can be unpredictable
	 * @param onlyFirstGroup - determine if there is should be matched only first group and all other skipped
	 * @param oneToOneLayerMatcher - layer to layer matcher
	 * @return layers splitted by matchign groups
	 */
	public static List<LayerGroup> matchLayers(
			final List<Layer> targetLayerList,
			final List<Layer> sourceLayerList,
			final boolean onlyFirstGroup,
			final BiFunction<Layer, Layer, Optional<Point2D>> oneToOneLayerMatcher
	)	{

		final List<Layer> layersForMatching = new ArrayList<>(targetLayerList);
		final List<LayerGroup> result = new ArrayList<>();
		LayerGroup layerGroup = new LayerGroup();
		boolean isFirstLayerGroup = true;	// for exclude sourceLayerList from layerGroup

		while(!layersForMatching.isEmpty()) {

			final Iterator<Layer> targetIterator = layersForMatching.iterator();
			boolean anyLayerMatched = false;

			while(targetIterator.hasNext()) {
				final Layer targetLayer = targetIterator.next();
				final boolean isMatched = matchLayers(targetLayer, layerGroup.getLayerGroup(), oneToOneLayerMatcher)
						|| (isFirstLayerGroup && matchLayers(targetLayer, sourceLayerList, oneToOneLayerMatcher));
				if(isMatched) {
					layerGroup.add(targetLayer);
					targetIterator.remove();
					anyLayerMatched = true;
				}
			}

			if(!anyLayerMatched)	{
				if(onlyFirstGroup)	{
					break;
				}
				if(!layerGroup.isEmpty()) {
					result.add(layerGroup);
				}
				isFirstLayerGroup = false;
				layerGroup = new LayerGroup();
				layerGroup.add(layersForMatching.remove(0));
			}

		}

		if(!layerGroup.isEmpty()) {
			result.add(layerGroup);
		}

		return result;

	}

	/**
	 * Performs search matches of the target layer within collection of the source layers.
	 * If matches found then target layer will be moved to its new position
	 * @param targetLayer - layer which will be moved to its matched position
	 * @param sourceLayerList - layers within which target will be search matches
	 * @param oneToOneLayerMatcher - layer to layer matcher
	 * @return true if matches for target layer found, false otherwise
	 */
	private static boolean matchLayers(
			final Layer targetLayer,
			final List<Layer> sourceLayerList,
			final BiFunction<Layer, Layer, Optional<Point2D>> oneToOneLayerMatcher
	) {

		for(final Layer sourceLayer : sourceLayerList) {

			if(oneToOneLayerMatcher
					.apply(sourceLayer, targetLayer)
					.map(offset -> {
						targetLayer.setX(targetLayer.getX() + offset.x());
						targetLayer.setY(targetLayer.getY() + offset.y());
						return offset;
					})
					.isPresent())	{
				return true;
			}

		}

		return false;

	}

	public static class LayerGroup	{

		private final List<Layer> layerGroup = new ArrayList<>();

		public LayerGroup() {
		}

		public LayerGroup(final List<Layer> source) {
			layerGroup.addAll(source);
		}

		public boolean add(final Layer layer) {
			return layerGroup.add(layer);
		}

		public List<Layer> getLayerGroup() {
			return Collections.unmodifiableList(layerGroup);
		}

		public boolean isEmpty()	{
			return layerGroup.isEmpty();
		}

	}

}
