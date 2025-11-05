# News Feed Mock API Server

Mock REST API server for the News Feed Android app. Uses [json-server](https://github.com/typicode/json-server) to serve mock data.

## Prerequisites

- **Node.js** (v14 or higher) - [Download here](https://nodejs.org/)
- **npm** (comes with Node.js)

## Quick Start

### 1. Install Dependencies

```bash
cd mock-server
npm install
```

### 2. Start the Server

**Option A: Using the startup script (Recommended)**
```bash
./start-server.sh
```

**Option B: Using npm directly**
```bash
npm start
```

The server will start on `http://localhost:3000`

## Connecting from Android

### Android Emulator
Use the special IP address: `http://10.0.2.2:3000`

### Physical Device
1. Make sure your phone and computer are on the same WiFi network
2. Use your computer's local IP address (shown when starting the server)
3. Example: `http://192.168.1.100:3000`

### Update the App

Edit `NetworkModule.kt` and change the BASE_URL:

```kotlin
private const val BASE_URL = "http://10.0.2.2:3000/"  // For emulator
// OR
private const val BASE_URL = "http://YOUR_LOCAL_IP:3000/"  // For physical device
```

## API Endpoints

### Get News Feed
```http
GET /api/feed?_page=0&_limit=20
```

Response:
```json
[
  {
    "postId": 1,
    "contentSummary": "...",
    "author": { "id": 101, "name": "Sarah Chen", ... },
    "createdAt": "2024-01-15T10:30:00Z",
    "liked": false,
    "likeCount": 42,
    "attachmentCount": 1,
    "attachmentPreviewImageUrl": "..."
  }
]
```

### Get Post Detail
```http
GET /api/posts/1
```

Response:
```json
{
  "postId": 1,
  "content": "Full post content...",
  "author": { ... },
  "createdAt": "2024-01-15T10:30:00Z",
  "liked": false,
  "likeCount": 42,
  "shareCount": 12,
  "attachments": [
    {
      "id": 1001,
      "type": "image",
      "contentUrl": "...",
      "previewImageUrl": "...",
      "caption": "..."
    }
  ]
}
```

### Create Post (Automatic)
```http
POST /api/posts
Content-Type: application/json

{
  "content": "New post content",
  "author": { ... },
  "attachments": []
}
```

### Interact with Post (Automatic)
```http
POST /api/posts/1/interact
Content-Type: application/json

{
  "requestId": 1,
  "type": "like"
}
```

## Features

✅ 10 sample posts with realistic data
✅ Mock images from placeholder services
✅ Profile pictures
✅ Multiple attachments per post
✅ Pagination support
✅ Auto-increments IDs
✅ Persists changes during session

## Mock Data

The mock data includes:
- **Feed**: 10 posts with various content types
- **Posts**: Detailed post data with full content and attachments
- **Authors**: 10 different users with profile pictures
- **Attachments**: Images with URLs from Lorem Picsum

## Customization

### Add More Posts

Edit `db.json` and add entries to the `feed` and `posts` arrays.

### Change Port

Edit `package.json`:
```json
{
  "scripts": {
    "start": "json-server ... --port 8080"
  }
}
```

### Enable CORS (if needed)

json-server enables CORS by default, but you can customize:
```bash
json-server --watch db.json --port 3000 --middlewares ./cors-middleware.js
```

## Troubleshooting

### Port Already in Use
```bash
# Kill process on port 3000
lsof -ti:3000 | xargs kill -9

# Or use a different port
npm start -- --port 8080
```

### Android Can't Connect to 10.0.2.2
- Make sure the mock server is running
- Try using your computer's actual IP address instead
- Check firewall settings

### Images Not Loading
The mock server uses public placeholder image services:
- [Lorem Picsum](https://picsum.photos/) for post images
- [Pravatar](https://i.pravatar.cc/) for profile pictures

These require internet connectivity.

## Development Tips

### Watch Mode
The server automatically reloads when you edit `db.json`

### Reset Data
Stop the server and restore `db.json` from backup

### Add Delay (Simulate Network Latency)
```bash
json-server --watch db.json --delay 1000  # 1 second delay
```

## Learn More

- [json-server Documentation](https://github.com/typicode/json-server)
- [Mobile System Design Interview Book](https://www.mobilesystemdesign.com/)

## License

MIT
