import axios from 'axios';

export interface CommentRequestDto {
  text: string;
  topicId: string;
  parentCommentId: string | null;
}

export interface ParentCommentResponseDto {
  id: string;
  content: string;
  text: string;
  topicId: string;
  authorId: string;
  authorName: string;
  createdAt: string;
  deleted: boolean;
  replyCount: number;
}

export interface ReplyResponseDto {
  id: string;
  content: string;
  text: string;
  parentId: string;
  topicId: string;
  authorId: string;
  authorName: string;
  userName?: string;
  createdAt: string;
  deleted: boolean;
}

export interface CommentPage {
  content: ParentCommentResponseDto[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ReplyPage {
  content: ReplyResponseDto[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
} 