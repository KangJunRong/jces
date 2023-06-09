package com.ecp.jces.server.dc.mapper.specification;

import com.ecp.jces.form.SpecificationForm;
import com.ecp.jces.vo.SpecificationVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SpecificationMapper {

    List<SpecificationVo> findList(SpecificationForm specificationForm);

    void insert(SpecificationForm specificationForm);

    void delete(SpecificationForm specificationForm);

    void updateStatus(SpecificationForm specificationForm);

    void downloadUpdate(SpecificationForm specificationForm);

    SpecificationVo get(SpecificationForm specificationForm);
}
