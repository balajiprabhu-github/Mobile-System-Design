/**
 * Script to generate 10,000 mock posts for testing pagination and performance
 */

const fs = require('fs');

// Sample content templates
const contentTemplates = [
  "Just finished reading an amazing book on {topic}! The {adjective} patterns are so elegant and practical.",
  "Beautiful {thing} from my {activity} today! Nature never fails to amaze me.",
  "Excited to announce that our team just launched a new {feature}! {tech} really made a difference.",
  "Coffee and code - my perfect {timeOfDay} routine! Working on some exciting {project} today.",
  "Had an amazing {event} today! Great {quality} and even better conversations.",
  "Just completed a {achievement}! {duration} of pure determination. Feeling accomplished!",
  "Working from this beautiful {place} today. The ambiance is perfect for productivity!",
  "New blog post: Understanding {topic}. Link in comments! Let me know what you think.",
  "Exploring the new {place}! So many amazing places to discover.",
  "{timeOfDay} vibes! Time to unwind after a productive {period}.",
  "Can't believe it's already {time}! Time flies when you're {activity}.",
  "Grateful for {thing} today. Sometimes we forget to appreciate the little things.",
  "Pro tip: {advice}. This has been a game changer for me!",
  "Weekend plans: {activity}. Who else is excited for {event}?",
  "Behind the scenes of {project}. It's been an incredible journey!",
  "Throwback to {time} when {event}. Good memories!",
  "Learning {skill} has been challenging but rewarding. Progress update: {status}",
  "Shoutout to {person} for {reason}. You're awesome!",
  "Quick update: {status}. Stay tuned for more!",
  "Celebrating {achievement} today! Couldn't have done it without {person}."
];

const topics = [
  "mobile system design", "Android development", "Kotlin coroutines", "Jetpack Compose",
  "software architecture", "machine learning", "web development", "cloud computing",
  "data science", "artificial intelligence", "blockchain", "cybersecurity"
];

const adjectives = [
  "elegant", "practical", "innovative", "efficient", "powerful", "intuitive",
  "scalable", "robust", "flexible", "modern", "sophisticated", "streamlined"
];

const things = [
  "sunset", "sunrise", "landscape", "cityscape", "mountain view", "ocean view",
  "garden", "artwork", "architecture", "design", "innovation", "creation"
];

const activities = [
  "hike", "walk", "run", "bike ride", "drive", "trip", "adventure",
  "exploration", "journey", "expedition", "experience", "venture"
];

const features = [
  "feature", "update", "product", "service", "tool", "platform",
  "solution", "application", "system", "framework", "library", "API"
];

const techs = [
  "Cache-first architecture", "Room database", "Kotlin Flow", "Coroutines",
  "Compose UI", "MVVM pattern", "Clean architecture", "Dependency injection",
  "Repository pattern", "LiveData", "ViewModel", "Navigation component"
];

const timeOfDays = ["morning", "afternoon", "evening", "night", "weekend"];
const projects = [
  "Jetpack Compose animations", "Kotlin multiplatform", "Android UI",
  "REST API integration", "GraphQL implementation", "database optimization"
];

const names = [
  "Sarah Chen", "Alex Kumar", "Maya Patel", "James Wilson", "Lisa Anderson",
  "Ryan Martinez", "Emma Thompson", "David Lee", "Sophie Garcia", "Michael Brown",
  "Emily Rodriguez", "Daniel Kim", "Olivia Nguyen", "Noah Jackson", "Ava White",
  "Ethan Davis", "Isabella Lopez", "Mason Taylor", "Sophia Anderson", "Lucas Moore"
];

function getRandomElement(array) {
  return array[Math.floor(Math.random() * array.length)];
}

function generateContent() {
  const template = getRandomElement(contentTemplates);
  return template
    .replace('{topic}', getRandomElement(topics))
    .replace('{adjective}', getRandomElement(adjectives))
    .replace('{thing}', getRandomElement(things))
    .replace('{activity}', getRandomElement(activities))
    .replace('{feature}', getRandomElement(features))
    .replace('{tech}', getRandomElement(techs))
    .replace('{timeOfDay}', getRandomElement(timeOfDays))
    .replace('{project}', getRandomElement(projects))
    .replace('{quality}', getRandomElement(adjectives))
    .replace('{event}', getRandomElement(activities))
    .replace('{achievement}', getRandomElement(features))
    .replace('{duration}', `${Math.floor(Math.random() * 12) + 1} months`)
    .replace('{place}', getRandomElement(things))
    .replace('{period}', getRandomElement(timeOfDays))
    .replace('{time}', getRandomElement(['Monday', 'Friday', 'the weekend']))
    .replace('{advice}', getRandomElement(techs))
    .replace('{status}', `${Math.floor(Math.random() * 100)}% complete`)
    .replace('{person}', getRandomElement(names).split(' ')[0])
    .replace('{reason}', getRandomElement(activities))
    .replace('{skill}', getRandomElement(topics));
}

function generatePost(id) {
  const authorId = (id % 20) + 101;
  const authorIndex = (id % 20);
  const hasImage = Math.random() > 0.3;
  const isLiked = Math.random() > 0.7;
  const likeCount = Math.floor(Math.random() * 500) + 1;
  const attachmentCount = hasImage ? Math.floor(Math.random() * 5) + 1 : 0;

  // Generate realistic timestamp (last 30 days)
  const daysAgo = Math.floor(Math.random() * 30);
  const hoursAgo = Math.floor(Math.random() * 24);
  const minutesAgo = Math.floor(Math.random() * 60);
  const date = new Date();
  date.setDate(date.getDate() - daysAgo);
  date.setHours(date.getHours() - hoursAgo);
  date.setMinutes(date.getMinutes() - minutesAgo);

  const content = generateContent();

  return {
    postId: id,
    contentSummary: content,
    author: {
      id: authorId,
      name: names[authorIndex],
      profileImageThumbnailUrl: `https://i.pravatar.cc/150?img=${(authorIndex % 70) + 1}`
    },
    createdAt: date.toISOString(),
    liked: isLiked,
    likeCount: likeCount,
    attachmentCount: attachmentCount,
    attachmentPreviewImageUrl: hasImage ? `https://picsum.photos/seed/post${id}/800/600` : null
  };
}

function generatePostDetail(id) {
  const feedPost = generatePost(id);
  const attachmentCount = feedPost.attachmentCount;
  const attachments = [];

  for (let i = 0; i < attachmentCount; i++) {
    const types = ['image', 'image', 'image', 'video']; // 75% images, 25% videos
    const type = getRandomElement(types);

    attachments.push({
      id: id * 100 + i + 1,
      type: type,
      contentUrl: type === 'image'
        ? `https://picsum.photos/seed/post${id}attachment${i}/800/600`
        : `https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_1mb.mp4`,
      previewImageUrl: `https://picsum.photos/seed/post${id}attachment${i}/400/300`,
      caption: Math.random() > 0.5 ? `Attachment ${i + 1} from post ${id}` : null
    });
  }

  return {
    postId: id,
    content: feedPost.contentSummary + `\n\nFull content for post ${id}. This is the detailed view with all the information about this post.`,
    author: feedPost.author,
    createdAt: feedPost.createdAt,
    liked: feedPost.liked,
    likeCount: feedPost.likeCount,
    shareCount: Math.floor(Math.random() * 100),
    attachments: attachments
  };
}

console.log('Generating 10,000 posts...');
console.log('This may take a minute...');

const feed = [];
const posts = [];

for (let i = 1; i <= 10000; i++) {
  feed.push(generatePost(i));
  posts.push(generatePostDetail(i));

  if (i % 1000 === 0) {
    console.log(`Generated ${i} posts...`);
  }
}

const db = {
  feed: feed,
  posts: posts,
  paging: {
    nextPageUrl: null,
    hasMore: false,
    pageSize: 20,
    totalCount: 10000
  }
};

console.log('Writing to db.json...');
fs.writeFileSync('db.json', JSON.stringify(db, null, 2));
console.log('✓ Successfully generated 10,000 posts!');
console.log('Restart the mock server to load the new data.');
