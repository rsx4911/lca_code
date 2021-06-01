package com.greendelta.collaboration.webservice.util;

import java.util.List;

import org.eclipse.jgit.lib.ObjectId;
import org.openlca.cloud.api.git.Datasets.Descriptor;
import org.openlca.cloud.api.git.DiffReference;
import org.openlca.cloud.api.git.Reference;
import org.openlca.cloud.api.git.References.Entry;
import org.openlca.cloud.api.git.References.EntryType;
import org.openlca.util.Strings;

import com.greendelta.collaboration.service.Repository;
import com.greendelta.collaboration.util.Collections;
import com.greendelta.collaboration.util.ObjectMap;

public class MetaData {

	public static ObjectMap toDatasetInfo(Entry e, Repository repo) {
		ObjectMap entry = ObjectMap.fromObject(e);
		entry.remove("objectId");
		if (e.typeOfEntry != EntryType.DATASET)
			return entry;
		putDatasetInfo(entry, e.objectId, repo);
		return entry;
	}

	public static ObjectMap toDatasetInfo(Reference r, Repository repo) {
		ObjectMap ref = ObjectMap.fromObject(r);
		ref.remove("objectId");
		putDatasetInfo(ref, r.objectId, repo);
		return ref;
	}

	public static ObjectMap toDatasetInfo(DiffReference d, Repository repo) {
		ObjectMap ref = toDatasetInfo(d.ref(), repo);
		ref.put("diffType", d.type);
		return ref;
	}

	private static void putDatasetInfo(ObjectMap entry, ObjectId oId, Repository repo) {
		Descriptor descriptor = repo.datasets.getDescriptor(oId);
		if (Strings.nullOrEmpty(descriptor.location)) {
			entry.put("name", descriptor.name);
		} else {
			entry.put("name", descriptor.name + " - " + descriptor.location);
		}
		if (descriptor.flowType != null) {
			entry.put("flowType", descriptor.flowType);
		}
		if (descriptor.processType != null) {
			entry.put("processType", descriptor.processType);
		}
	}

	public static void sortByName(List<ObjectMap> data) {
		Collections.sort(data, (m1, m2) -> {
			return m1.getString("name").toLowerCase().compareTo(m2.getString("name").toLowerCase());
		});
	}

	public static void sortByType(List<ObjectMap> data, List<String> typesOrder) {
		Collections.sort(data, (m1, m2) -> {
			String t1 = m1.getString("type");
			String t2 = m2.getString("type");
			return Integer.compare(typesOrder.indexOf(t1), typesOrder.indexOf(t2));
		});
	}

	public static void sortByTypeAndName(List<ObjectMap> data, List<String> typesOrder) {
		Collections.sort(data, (m1, m2) -> {
			String t1 = m1.getString("type");
			String t2 = m2.getString("type");
			if (!t1.equals(t2))
				return Integer.compare(typesOrder.indexOf(t1), typesOrder.indexOf(t2));
			return m1.getString("name").toLowerCase().compareTo(m2.getString("name").toLowerCase());
		});
	}

}
