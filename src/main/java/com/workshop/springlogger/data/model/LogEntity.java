package com.workshop.springlogger.data.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "LogEntity")
@Table(name = "log")
@Data
@Builder
public class LogEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "log_message", nullable = false, length = 4096)
    private String logMessage;
    @Column(name = "application_name", nullable = false, length = 80)
    private String applicationName;
    @Column(name = "log_level", nullable = false, length = 80)
    private String logLevel;
    @Column(name = "log_date", nullable = false)
    private LocalDateTime logDate;

}
