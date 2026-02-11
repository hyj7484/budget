"use client";

import { useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";

const loginSchema = z.object({
  email: z.string().email("이메일 형식이 올바르지 않습니다."),
  password: z.string().min(1, "비밀번호를 입력하세요."),
  remember: z.boolean().optional(),
});

type LoginForm = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const router = useRouter();
  const [showPw, setShowPw] = useState(false);
  const [serverError, setServerError] = useState<string | null>(null);

  const defaultValues = useMemo<LoginForm>(
    () => ({ email: "", password: "", remember: true }),
    [],
  );

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
    defaultValues,
  });

  const onSubmit = async (data: LoginForm) => {
    setServerError(null);

    // ✅ Spring으로 프록시되는 엔드포인트 (Next가 /api 를 8000으로 넘김)
    // Spring 측에서 Set-Cookie(HttpOnly) 방식으로 세션/토큰 내려주는 걸 추천
    const res = await fetch("/api/v1/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      credentials: "include", // 쿠키 기반 인증을 위해
      body: JSON.stringify({
        email: data.email,
        password: data.password,
        remember: !!data.remember,
      }),
    });

    if (!res.ok) {
      // 서버가 {message} 형태로 내려준다고 가정
      let msg = "로그인에 실패했습니다.";
      try {
        const json = await res.json();
        if (typeof json?.message === "string") msg = json.message;
      } catch {
        // ignore
      }
      setServerError(msg);
      return;
    }

    // 성공 시: 대시보드로 이동(없으면 원하는 페이지로 변경)
    router.push("/dashboard");
  };

  return (
    <div className="bg-white rounded-2xl shadow p-8">
      <h1 className="text-2xl font-semibold text-gray-900">로그인</h1>
      <p className="text-sm text-gray-500 mt-1">가계부에 접속하세요.</p>

      <form className="mt-6 space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="block text-sm font-medium text-gray-700">
            이메일
          </label>
          <input
            type="email"
            className="mt-1 w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:ring-2 focus:ring-gray-300"
            placeholder="you@example.com"
            {...register("email")}
          />
          {errors.email && (
            <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>
          )}
        </div>

        <div>
          <label className="block text-sm font-medium text-gray-700">
            비밀번호
          </label>
          <div className="mt-1 flex gap-2">
            <input
              type={showPw ? "text" : "password"}
              className="w-full rounded-lg border border-gray-300 px-3 py-2 outline-none focus:ring-2 focus:ring-gray-300"
              placeholder="••••••••"
              {...register("password")}
            />
            <button
              type="button"
              className="px-3 py-2 rounded-lg border border-gray-300 text-sm text-gray-700 hover:bg-gray-50"
              onClick={() => setShowPw((v) => !v)}
            >
              {showPw ? "숨김" : "보기"}
            </button>
          </div>
          {errors.password && (
            <p className="mt-1 text-sm text-red-600">
              {errors.password.message}
            </p>
          )}
        </div>

        <div className="flex items-center justify-between">
          <label className="flex items-center gap-2 text-sm text-gray-700">
            <input
              type="checkbox"
              className="h-4 w-4"
              {...register("remember")}
            />
            로그인 유지
          </label>

          <a
            className="text-sm text-gray-600 hover:underline"
            href="/forgot-password"
          >
            비밀번호 찾기
          </a>
        </div>

        {serverError && (
          <div className="rounded-lg bg-red-50 border border-red-200 p-3 text-sm text-red-700">
            {serverError}
          </div>
        )}

        <button
          type="submit"
          disabled={isSubmitting}
          className="w-full rounded-lg bg-gray-900 text-white py-2.5 font-medium hover:bg-gray-800 disabled:opacity-60"
        >
          {isSubmitting ? "로그인 중..." : "로그인"}
        </button>

        <div className="text-sm text-gray-600 text-center">
          계정이 없나요?{" "}
          <a
            className="font-medium text-gray-900 hover:underline"
            href="/signup"
          >
            회원가입
          </a>
        </div>
      </form>
    </div>
  );
}
