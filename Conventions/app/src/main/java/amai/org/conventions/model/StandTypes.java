package amai.org.conventions.model;

import java.util.ArrayList;
import java.util.List;

import sff.org.conventions.R;
import amai.org.conventions.utils.CollectionUtils;

public class StandTypes {
	private List<StandType> standTypes;

	public StandTypes(List<StandType> standTypes) {
		// The stand types list can be modified, and we don't know if the sent stand types list is mutable,
		// so we copy it to a new array list
		this.standTypes = new ArrayList<>(standTypes);
	}

	public List<StandType> getStandTypes() {
		return standTypes;
	}

	public StandType findByName(final String name) {
		return CollectionUtils.findFirst(standTypes, new CollectionUtils.Predicate<StandType>() {
			@Override
			public boolean where(StandType standType) {
				return standType.getName().equalsIgnoreCase(name);
			}
		});
	}

	// required in case the server added stand types which weren't a part of the app's pre-defined stand types
	public StandType add(String name) {
		StandType st = new StandType().withName(name).withImage(R.drawable.ic_shopping_basket).withOrder(getHighestStandTypeOrder() + 1);
		standTypes.add(st);
		return st;
	}

	private int getHighestStandTypeOrder() {
		int maxStandTypeOrder = -1;
		for (StandType standType : standTypes) {
			maxStandTypeOrder = Math.max(maxStandTypeOrder, standType.getOrder());
		}

		return maxStandTypeOrder;
	}
}
