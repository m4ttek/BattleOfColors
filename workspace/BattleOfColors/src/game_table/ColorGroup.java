package game_table;

import java.awt.Point;
import java.util.LinkedList;
import java.util.Set;

public class ColorGroup {
	private static int count;
	private int uid;
	private LinkedList<Point> fieldsInGroup;
	
	public ColorGroup(Point initialField) {
		uid = ++count;
		//System.out.println("Group " + uid + " created");
		fieldsInGroup = new LinkedList<Point>();
		fieldsInGroup.add(initialField);
	}
	
	public void add(Point field) {
		fieldsInGroup.add(field);
	}
	
	private void clear() {
		fieldsInGroup.clear();
	}
	
	public int getUid() {
		return uid;
	}
	
	public static void resetCount() {
		count = 0;
	}
	
	public LinkedList<Point> getFieldsInGroup() {
		return fieldsInGroup;
	}
	
	public void join(ColorGroup colorGroup, ColorGroup[][] groups,
			Set<ColorGroup> firstPlayerGroups, Set<ColorGroup> secondPlayerGroups ) {
		for(Point field : colorGroup.fieldsInGroup) {
			add(field);
			groups[field.y][field.x] = this;
		}
		
		boolean addThisGroup = false;
		
		for(ColorGroup group : firstPlayerGroups) {
			if(group == colorGroup) {
				addThisGroup = true;
				break;
			}
		}
		
		if(addThisGroup) {
			firstPlayerGroups.remove(colorGroup);
			firstPlayerGroups.add(this);
			addThisGroup = false;
		}
		
		for(ColorGroup group : secondPlayerGroups) {
			if(group == colorGroup) {
				addThisGroup = true;
				break;
			}
		}
		
		if(addThisGroup) {
			secondPlayerGroups.remove(colorGroup);
			secondPlayerGroups.add(this);
		}
		colorGroup.clear();
	}
}