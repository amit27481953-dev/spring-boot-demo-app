package com.amit.spring.event;

import com.amit.spring.doc.ProductDoc;
import com.amit.spring.enu.Operation;

public record ProductChangeEvent(String sku , ProductDoc productDoc, Operation operation) {
}
