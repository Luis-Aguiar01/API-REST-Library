package com.luis.aguiar.mappers;

import com.luis.aguiar.dto.LoanRequestDto;
import com.luis.aguiar.dto.LoanResponseDto;
import com.luis.aguiar.models.Loan;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoanMapper {

    private final static ModelMapper mapper = new ModelMapper();

    public static Loan toLoan(LoanResponseDto dto) {
        return mapper.map(dto, Loan.class);
    }

    public static Loan toLoan(LoanRequestDto dto) {
        return mapper.map(dto, Loan.class);
    }

    public static LoanResponseDto toResponseDto(Loan loan) {
        return mapper.map(loan, LoanResponseDto.class);
    }
}
