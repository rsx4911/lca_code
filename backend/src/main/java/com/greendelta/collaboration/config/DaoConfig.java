package com.greendelta.collaboration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.greendelta.collaboration.model.Comment;
import com.greendelta.collaboration.model.Membership;
import com.greendelta.collaboration.model.Message;
import com.greendelta.collaboration.model.ReleaseInfo;
import com.greendelta.collaboration.model.Team;
import com.greendelta.collaboration.model.User;
import com.greendelta.collaboration.model.job.Job;
import com.greendelta.collaboration.model.settings.Setting;
import com.greendelta.collaboration.model.task.Review;
import com.greendelta.collaboration.model.task.ReviewReference;
import com.greendelta.collaboration.model.task.Task;
import com.greendelta.collaboration.model.task.TaskAssignment;
import com.greendelta.collaboration.service.Dao;

@Configuration
public class DaoConfig {

    @Bean
    Dao<Job> jobDao() {
		return new Dao<>(Job.class);
	}

    @Bean
    Dao<Setting> settingDao() {
		return new Dao<>(Setting.class);
	}

    @Bean
    Dao<Review> reviewDao() {
		return new Dao<>(Review.class);
	}

    @Bean
    Dao<ReviewReference> reviewReferenceDao() {
		return new Dao<>(ReviewReference.class);
	}

    @Bean
    Dao<Task> taskDao() {
		return new Dao<>(Task.class);
	}

    @Bean
    Dao<TaskAssignment> taskAssignmentDao() {
		return new Dao<>(TaskAssignment.class);
	}

    @Bean
    Dao<Comment> commentDao() {
		return new Dao<>(Comment.class);
	}

    @Bean
    Dao<Membership> membershipDao() {
		return new Dao<>(Membership.class);
	}

    @Bean
    Dao<ReleaseInfo> releaseInfoDao() {
		return new Dao<>(ReleaseInfo.class);
	}

    @Bean
    Dao<Message> messageDao() {
		return new Dao<>(Message.class);
	}

    @Bean
    Dao<Team> teamDao() {
		return new Dao<>(Team.class);
	}

    @Bean
    Dao<User> userDao() {
		return new Dao<>(User.class);
	}

}
