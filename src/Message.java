/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */
class Message {
     int message_id;
     User from;
     int date;
     User chat;
     User forward_from;
     int forward_date;
     Message reply_to_message;
     String text;
     Audio audio;
     Document document;
     PhotoSize[] photo;
     Sticker sticker;
     Video video;
     Contact contact;
     Location location;
     User new_chat_participant;
     User left_chat_participant;
     String new_chat_title;
     PhotoSize new_chat_photo;
     boolean delete_chat_photo;
     boolean group_chat_created;
 

    @Override
    public String toString() {
        return "UpdateMessage{" + ", message_id=" + message_id + ", from=" + from + ", date=" + date + ", chat=" + chat + ", forward_from=" + forward_from + ", forward_date=" + forward_date + ", reply_to_message=" + reply_to_message + ", text=" + text + ", audio=" + audio + ", document=" + document + ", photo=" + photo + ", sticker=" + sticker + ", video=" + video + ", contact=" + contact + ", location=" + location + ", new_chat_participant=" + new_chat_participant + ", left_chat_participant=" + left_chat_participant + ", new_chat_title=" + new_chat_title + ", new_chat_photo=" + new_chat_photo + ", delete_chat_photo=" + delete_chat_photo + ", group_chat_created=" + group_chat_created + '}';
    }
    
    
}
