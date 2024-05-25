package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.LoanResponseDto;
import com.luis.aguiar.models.Loan;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanMapper {

    private final static ModelMapper mapper = new ModelMapper();

    public static LoanResponseDto toResponseDto(Loan loan) {
        if (loan == null) {
            throw new IllegalArgumentException("The Loan can't be null.");
        }
        return mapper.map(loan, LoanResponseDto.class);
    }
}