import { PUBLIC_BACKEND_URL } from '$env/static/public';
import { ContentType, RequestError, ResponseType } from './types.svelte';

export const post = async <T>(
  path: string,
  res_type: ResponseType,
  content_type: ContentType,
  body: any,
  signal?: AbortSignal
): Promise<T | RequestError> => {
  return await request(path, 'POST', res_type, content_type, body, signal);
};

export const get = async <T>(
  path: string,
  res_type: ResponseType,
  signal?: AbortSignal
): Promise<T | RequestError> => {
  return await request(path, 'GET', res_type, undefined, undefined, signal);
};

export const put = async <T>(
  path: string,
  res_type: ResponseType,
  content_type: ContentType,
  body: any,
  signal?: AbortSignal
): Promise<T | RequestError> => {
  return await request(path, 'PUT', res_type, content_type, body, signal);
};

export const deletee = async <T>(
  path: string,
  res_type: ResponseType,
  body: any,
  signal?: AbortSignal
): Promise<T | RequestError> => {
  return await request(path, 'DELETE', res_type, body, signal);
};

const request = async <T>(
  path: string,
  method: string,
  res_type: ResponseType,
  content_type?: ContentType,
  body?: any,
  signal?: AbortSignal
): Promise<T | RequestError> => {
  let headers;
  if (content_type && body) {
    headers = {
      'Content-Type': content_type
    };
  }

  try {
    let res = await fetch!(`${PUBLIC_BACKEND_URL}${path}`, {
      method,
      headers,
      body,
      signal
    });

    switch (res.status) {
      case 200:
      case 201:
        break;
      case 400:
        return RequestError.BadRequest;
      case 404:
        return RequestError.NotFound;
      default:
        return RequestError.Other;
    }

    switch (res_type) {
      case ResponseType.Json:
        let json = await res.json();
        return json as T;
      case ResponseType.Text:
        let text = await res.text();
        return text as T;
      case ResponseType.None:
        return undefined as T;
    }
  } catch (_) {
    return RequestError.Other;
  }
};
