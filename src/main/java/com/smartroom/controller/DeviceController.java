package com.smartroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartroom.model.Device;
import com.smartroom.repository.Repository;

public class DeviceController extends Controller<Device> {
    public DeviceController(Repository<Device> repository,
                            ObjectMapper mapper) {
        super(repository, mapper, Device.class, "/api/devices");
    }
}
