package tp1.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;


public class EntrySort<K,V> implements Map.Entry<K, V>, Comparable<EntrySort<K,V>> {

	V value;
	K key;
	
	public EntrySort (K key, V value){
		this.key = key;
		this.value = value;
	}
	
	@Override
	public int compareTo(EntrySort<K,V> o) {
		return (Integer)value - (Integer)o.getValue();
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return value;
	}

	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
	
	public static <K,V> List<EntrySort<K,V>> toEntrySort (Collection<Map.Entry<K, V>> set) {
		List<EntrySort<K,V>> retSet = new ArrayList<>(set.size());
		for (Map.Entry<K,V> entry : set) {
			retSet.add(new EntrySort<K,V>(entry.getKey(),entry.getValue()));
		}
		retSet.sort(new Comparator<EntrySort<K,V>>() {

			@Override
			public int compare(EntrySort<K, V> o1, EntrySort<K, V> o2) {
				return o1.compareTo(o2);
			}
		});
		return retSet;
		
		
	}

}
