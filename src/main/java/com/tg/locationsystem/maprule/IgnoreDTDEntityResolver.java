package com.tg.locationsystem.maprule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.tg.locationsystem.entity.Map;
import org.apache.ibatis.annotations.Param;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class IgnoreDTDEntityResolver implements EntityResolver {

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

        return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
    }

}









