package amai.org.conventions.model;

import java.util.ArrayList;
import java.util.List;

import amai.org.conventions.utils.CollectionUtils;

public class Halls {
	private List<Hall> halls;

	public Halls(List<Hall> halls) {
		// The halls list can be modified, and we don't know if the sent halls list is mutable,
		// so we copy it to a new array list
		this.halls = new ArrayList<>(halls);
	}

	public List<Hall> getHalls() {
		return halls;
	}

	public Hall findByName(final String name) {
		return CollectionUtils.findFirst(halls, new CollectionUtils.Predicate<Hall>() {
			@Override
			public boolean where(Hall hall) {
				return hall.getName().equalsIgnoreCase(name);
			}
		});
	}

	// required in case the server added halls which weren't a part of the app's pre-defined halls
	public Hall add(String name) {
		Hall hall = new Hall().withName(name).withOrder(getHighestHallOrder() + 1);
		halls.add(hall);
		return hall;
	}

	private int getHighestHallOrder() {
		int maxHallOrder = -1;
		for (Hall hall : halls) {
			maxHallOrder = Math.max(maxHallOrder, hall.getOrder());
		}

		return maxHallOrder;
	}
}
