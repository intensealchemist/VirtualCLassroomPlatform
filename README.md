# 🎓 Virtual Classroom Platform

A comprehensive, modern virtual learning management system built with **Spring Boot** and designed for seamless online education experiences.

## ✨ Features

### 🔐 **Authentication & Authorization**
- JWT-based secure authentication
- Role-based access control (Student, Instructor, Admin)
- User profile management
- Password reset functionality

### 📚 **Course Management**
- Create and manage courses with rich content
- Lesson organization with video support
- Assignment creation and submission system
- Course enrollment and progress tracking
- File upload/download for assignments

### 💬 **Real-Time Communication**
- WebSocket-powered live chat system
- Course-specific discussion forums
- Threaded conversations and replies
- Real-time notifications
- In-app messaging system

### 📊 **Analytics & Dashboard**
- **Student Dashboard**: Progress tracking, upcoming assignments, course overview
- **Instructor Dashboard**: Student analytics, course statistics, engagement metrics
- **Admin Dashboard**: Platform-wide statistics and user management
- Comprehensive progress tracking and reporting

### 🔔 **Notification System**
- Real-time push notifications
- Email notifications for important events
- Assignment due date reminders
- Course enrollment confirmations
- Discussion reply notifications

### 🎯 **Learning Management**
- Interactive lesson progress tracking
- Assignment grading and feedback
- Course completion certificates
- Student performance analytics
- Customizable learning paths

## 🛠️ **Technology Stack**

### Backend
- **Framework**: Spring Boot 3.2.5
- **Language**: Java 17
- **Security**: Spring Security with JWT
- **Database**: MySQL with JPA/Hibernate
- **Real-time**: WebSocket (STOMP)
- **API Documentation**: OpenAPI 3 (Swagger)
- **Build Tool**: Maven

### Frontend
- **Template Engine**: Thymeleaf
- **Styling**: Bootstrap 4
- **JavaScript**: jQuery
- **Real-time**: SockJS + STOMP
- **Video Integration**: Agora RTC
- **Interactive Features**: Whiteboard support

## 🚀 **Getting Started**

### Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/VirtualCLass-1.git
   cd VirtualCLass-1
   ```

2. **Configure Database**
   ```properties
   # Update src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/virtual_classroom
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and Run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

4. **Access the Application**
   - Main Application: http://localhost:4100
   - API Documentation: http://localhost:4100/swagger-ui.html
   - Admin Panel: http://localhost:4100/admin

## 📋 **API Endpoints**

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Token refresh

### Courses
- `GET /api/courses` - List all courses
- `POST /api/courses` - Create new course
- `GET /api/courses/{id}` - Get course details
- `PUT /api/courses/{id}` - Update course
- `DELETE /api/courses/{id}` - Delete course

### Lessons & Assignments
- `GET /api/lessons/course/{courseId}` - Get course lessons
- `POST /api/lessons` - Create lesson
- `GET /api/assignments/course/{courseId}` - Get course assignments
- `POST /api/assignments` - Create assignment

### Real-time Communication
- `WebSocket: /ws` - Chat and notifications
- `GET /api/chat/room/{roomId}` - Get chat history
- `POST /api/chat/send` - Send message
- `GET /api/notifications` - Get user notifications

### Analytics
- `GET /api/dashboard/student` - Student dashboard
- `GET /api/dashboard/instructor` - Instructor dashboard
- `GET /api/dashboard/admin/stats` - Admin statistics

## 🎨 **Key Features Highlights**

### 📱 **Responsive Design**
- Mobile-friendly interface
- Cross-browser compatibility
- Adaptive layouts for all devices

### 🔒 **Security**
- JWT token-based authentication
- CORS configuration
- Input validation and sanitization
- Role-based endpoint protection

### ⚡ **Performance**
- Optimized database queries
- Lazy loading for large datasets
- Efficient file handling
- Real-time updates without page refresh

### 🎓 **Educational Tools**
- Interactive whiteboard
- Video conferencing integration
- Assignment submission system
- Progress tracking and analytics
- Discussion forums

## 👥 **User Roles**

### 🎓 **Students**
- Enroll in courses
- Access lessons and materials
- Submit assignments
- Track progress
- Participate in discussions
- Receive notifications

### 👨‍🏫 **Instructors**
- Create and manage courses
- Upload lessons and materials
- Create assignments
- Grade submissions
- Monitor student progress
- Moderate discussions

### 🛡️ **Administrators**
- Manage all users and courses
- View platform analytics
- System configuration
- User role management
- Platform monitoring

## 🤝 **Contributing**

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 **Support**

For support and questions:
- 📧 Email: support@virtualclassroom.com
- 💬 Discord: [Join our community](https://discord.gg/virtualclassroom)
- 📖 Documentation: [Wiki](https://github.com/yourusername/VirtualCLass-1/wiki)

---

**Built with ❤️ for modern education**
