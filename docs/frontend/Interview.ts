export interface EvaluationRequest {
  question: string;
  userResponse: string;
}

export interface EvaluationResult {
  score: number;
  strengths: string[];
  codeSmells: string[];
  feedback?: string;
  technicalFeedback: string;
  suggestedImprovement: string;
}

export const evaluateInterviewResponse = async (
  token: string,
  payload: EvaluationRequest,
  baseUrl = 'http://localhost:8081'
): Promise<EvaluationResult> => {
  const response = await fetch(`${baseUrl}/api/v1/interview/evaluate`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    throw new Error(`Interview evaluation failed: ${response.status}`);
  }

  return response.json() as Promise<EvaluationResult>;
};


