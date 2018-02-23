package com.socioseer.common.domain.model.campaign;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author OrangeMantra
 * @since JDK 1.8
 * @version 1.0
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Budget {
    private String platformId;
	private String platform;
	private String duration;
	private BigDecimal budget;
	private long startDate;
    private long endDate;
    private String currency;
    
}
