package ru.yandex.practicum.filmorate.storage;


import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class InMemoryFriendStorage implements FriendStorage {

    private final Set<Friendship> friendships;

    public InMemoryFriendStorage() {
        this.friendships = new HashSet<>();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        Friendship friendship = Friendship.builder()
                .userIdOne(Long.min(userId, friendId))
                .userIdTwo(Long.max(userId, friendId)).build();
        friendships.add(friendship);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        Friendship friendship = Friendship.builder()
                .userIdOne(Long.min(userId, friendId))
                .userIdTwo(Long.max(userId, friendId)).build();
        friendships.remove(friendship);
    }

    @Override
    public Set<Long> getIdsFriendsForUser(long userId) {
        Set<Long> ids = new HashSet<>();
        friendships.forEach(friendship -> {
            if (friendship.getUserIdOne() == userId) {
                ids.add(friendship.getUserIdTwo());
            }
            if (friendship.getUserIdTwo() == userId) {
                ids.add(friendship.getUserIdOne());
            }
        });
        return ids;
    }

    @Override
    public Set<Long> getMutualFriendsOfUsers(long userId, long otherId) {
        return getIdsFriendsForUser(userId)
                .stream()
                .filter(id -> getIdsFriendsForUser(otherId).contains(id))
                .collect(Collectors.toSet());
    }
}
