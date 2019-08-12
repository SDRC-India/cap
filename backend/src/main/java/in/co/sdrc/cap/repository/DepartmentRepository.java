package in.co.sdrc.cap.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import in.co.sdrc.cap.domain.Department;

@Repository
public interface DepartmentRepository extends org.springframework.data.repository.Repository<Department, Integer> {

	Department save(Department department);

	Department findOne(int department);

	List<Department> findAll();

	Department findByDepartmentName(String department);
}