package Companies;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CompaniesRepository extends JpaRepository<CompaniesModel, UUID> {
}
