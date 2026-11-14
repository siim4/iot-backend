package com.smartroom.repository;

import com.smartroom.model.Device;
import java.util.List;
import java.util.UUID;

public interface DeviceRepository
	extends Repository<Device> {

	List<Device> findByOwnerId(UUID ownerId) throws Exception;
}
