package amai.org.conventions.map;

import amai.org.conventions.model.MapLocation;
import amai.org.conventions.utils.CollectionUtils;
import amai.org.conventions.utils.Objects;

class MapLocationSearchEquality implements CollectionUtils.EqualityPredicate<MapLocation> {
	@Override
	public boolean equals(MapLocation lhs, MapLocation rhs) {
		return Objects.equals(lhs.getFloor(), rhs.getFloor()) &&
				Objects.equals(lhs.getName(), rhs.getName()) &&
				lhs.hasSinglePlace() == rhs.hasSinglePlace() &&
				lhs.areAllPlacesHalls() == rhs.areAllPlacesHalls();
	}
}
